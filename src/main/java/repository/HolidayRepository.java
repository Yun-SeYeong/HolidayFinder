package repository;

import dto.HolidayDto;
import dto.MaxCountHolidayDto;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class HolidayRepository {

  private static final String END_POINT = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/";
  private static final HashMap<String, List<HolidayDto>> cache = new HashMap<>();
  private final String OPEN_API_KEY;
  private final CloseableHttpClient httpClient;
  private final DocumentBuilder documentBuilder;

  public HolidayRepository(String key)
      throws ParserConfigurationException, IOException, ClassNotFoundException {
    // 인스턴스 초기화 및 캐시파일 로드
    this.OPEN_API_KEY = key;
    httpClient = HttpClients.createDefault();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    documentBuilder = factory.newDocumentBuilder();
    loadCache();
  }

  /**
   * 캐시여부에 따라 캐시된 데이터를 조회하거나 공공데이터 포털에서 다운로드 받아 공휴일 정보를 반환한다.
   * @param year 조회할 연도
   * @param month 조회할 월
   * @return 조회한 공휴일 정보
   */
  public List<HolidayDto> getHoliday(String year, String month) throws IOException, SAXException {
    // 캐시여부에 따라 데이터를 파일로 부터 읽어온다.
    List<HolidayDto> cachedHoliday = getCachedHoliday(year, month);
    if (cachedHoliday != null) {
      return cachedHoliday;
    }

    // 공공데이터 포털로부터 데이터를 다운로드한다.
    System.out.printf("%s년 %s월 데이터 다운로드 중...\n", year, month);
    System.out.flush();

    HttpGet holidayGet = new HttpGet(END_POINT
        + "getRestDeInfo?serviceKey=" + OPEN_API_KEY
        + "&solYear=" + year
        + "&solMonth=" + month);
    CloseableHttpResponse httpResponse = httpClient.execute(holidayGet);

    if (httpResponse.getCode() != HttpStatus.SC_OK) {
      throw new IOException("API response code: " + httpResponse.getCode());
    }

    // 다운로드한 데이터를 파싱하여 DTO에 넣고 일별로 Map에 저장한다.
    Document document = documentBuilder.parse(httpResponse.getEntity().getContent());
    Element root = document.getDocumentElement();
    Node items = getNodeByPath(root, "response.body.items");

    if (items == null) {
      throw new IllegalArgumentException("Cannot parsing response.");
    }

    ArrayList<HolidayDto> holidayDtoArrayList = new ArrayList<>();

    for (int i = 0; i < items.getChildNodes().getLength(); i++) {
      Node item = items.getChildNodes().item(i);

      HolidayDto holidayDto = new HolidayDto(LocalDateTime.now());
      for (int j = 0; j < item.getChildNodes().getLength(); j++) {
        Node node = item.getChildNodes().item(j);
        switch (node.getNodeName()) {
          case "dateKind":
            holidayDto.setDateKind(node.getTextContent());
            break;
          case "dateName":
            holidayDto.setDateName(node.getTextContent());
            break;
          case "isHoliday":
            holidayDto.setHoliday(node.getTextContent().equals("Y"));
            break;
          case "locdate":
            holidayDto.setLocDate(node.getTextContent());
            break;
        }
      }
      holidayDtoArrayList.add(holidayDto);
    }

    // Map에 DTO를 넣고 파일에 저장한다.
    cache.put(year + month, holidayDtoArrayList);
    saveCache();

    return holidayDtoArrayList;
  }

  /**
   * 해당 연도의 가장 휴일이 많은 달의 공휴일 정보와 해당 연도를 반한한다.
   * @param year 조회할 연도
   * @return 가장 휴일이 많은 연도의 공휴일 정보
   */
  public MaxCountHolidayDto getMaxCountHolidayByYear(String year) throws IOException, SAXException {
    ArrayList<HolidayDto> holidayDtos = new ArrayList<>();
    int maxMonth = 0;

    for (int i = 1; i <= 12; i++) {
      List<HolidayDto> holiday = getHoliday(year, String.format("%02d", i));

      if (holiday.size() > holidayDtos.size()) {
        holidayDtos.clear();
        holidayDtos.addAll(holiday);
        maxMonth = i;
      }
    }

    return new MaxCountHolidayDto(maxMonth, holidayDtos);
  }

  /**
   * 해당 연도의 공휴일 정보를 모두 조회한다.
   * @param year 조회할 연도
   * @return 조회한 공휴일 정보
   */
  public List<HolidayDto> getHolidayByYear(String year) throws IOException, SAXException {
    ArrayList<HolidayDto> holidayDtos = new ArrayList<>();

    for (int i = 1; i <= 12; i++) {
      holidayDtos.addAll(getHoliday(year, String.format("%02d", i)));
    }

    return holidayDtos;
  }

  /**
   * 해당 연도의 공휴일 정보를 월별로 그룹핑하여 Map으로 반환한다.
   * @param year 조회할 연도
   * @return 월별 공휴일 정보
   */
  public Map<String, List<HolidayDto>> getHolidayMapByYear(String year)
      throws IOException, SAXException {
    Map<String, List<HolidayDto>> holidayMap = new HashMap<>();

    for (int i = 1; i <= 12; i++) {
      holidayMap.put(year + String.format("%02d", i), getHoliday(year, String.format("%02d", i)));
    }

    return holidayMap;
  }

  /**
   * 캐시된 공휴일 정보를 조회한다.
   * @param year 조회할 연도
   * @param month 조회할 월
   * @return 조회된 공휴일 정보
   */
  private List<HolidayDto> getCachedHoliday(String year, String month) {
    return cache.get(year + month);
  }

  /**
   * 캐시데이터를 파일에 저자한다.
   */
  private void saveCache() throws IOException {
    ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get("cache")));
    oos.writeObject(cache);
    oos.close();
  }

  /**
   * 캐시파일을 로드한다.
   */
  private void loadCache() throws IOException, ClassNotFoundException {
    File cacheFile = new File("cache");
    if (cacheFile.exists()) {
      ObjectInputStream oos = new ObjectInputStream(Files.newInputStream(cacheFile.toPath()));
      cache.putAll((HashMap<String, List<HolidayDto>>) oos.readObject());
      oos.close();
    }
  }

  /**
   * 캐시파일을 삭제한다.
   */
  public void deleteCache() {
    File cacheFile = new File("cache");
    if (cacheFile.exists()) {
      boolean delete = cacheFile.delete();
      if (delete) {
        cache.clear();
      }
    }
  }

  private Node getNodeByPath(Node root, String paths) {
    String[] split = paths.split("\\.");
    int count = 0;
    for (String path : split) {
      for (int i = 0; i < root.getChildNodes().getLength(); i++) {
        Node node = root.getChildNodes().item(i);
        if (node.getNodeName().equals(path)) {
          root = node;
          count++;
        }
      }
    }
    return count >= split.length - 1 ? root : null;
  }
}
