package dto;

import java.util.List;

public class MaxCountHolidayDto {

  private final int maxYear;
  private final List<HolidayDto> holidayDtos;

  public MaxCountHolidayDto(int maxYear, List<HolidayDto> holidayDtos) {
    this.maxYear = maxYear;
    this.holidayDtos = holidayDtos;
  }

  public int getMaxYear() {
    return maxYear;
  }

  public List<HolidayDto> getHolidayDtos() {
    return holidayDtos;
  }
}
