package repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HolidayRepositoryTest {

  @Test
  @DisplayName(value = "GET Holiday Test")
  public void getHolidayTest() throws Exception {
    // given
    String key = "922T%2FggEzFLCf3DO224yyCR0geVmU6X1KwM%2FDq%2Bp%2FcW6UueOtxRERU7eEJgiOnIyv5MvbcsOJd6HeRdYZwO1Hw%3D%3D";
    String year = "2023";
    String month = "05";
    HolidayRepository holidayRepository = new HolidayRepository(key);

    // when
    Assertions.assertNotNull(holidayRepository.getHoliday(year, month));
  }
}