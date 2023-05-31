package dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HolidayDto implements Serializable {
  private String dateKind;
  private String dateName;
  private Boolean isHoliday;
  private String locDate;
  private LocalDateTime createdAt;

  public HolidayDto(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getDateKind() {
    return dateKind;
  }

  public void setDateKind(String dateKind) {
    this.dateKind = dateKind;
  }

  public String getDateName() {
    return dateName;
  }

  public void setDateName(String dateName) {
    this.dateName = dateName;
  }

  public Boolean getHoliday() {
    return isHoliday;
  }

  public void setHoliday(Boolean holiday) {
    isHoliday = holiday;
  }

  public String getLocDate() {
    return locDate;
  }

  public void setLocDate(String locDate) {
    this.locDate = locDate;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return "HolidayDto{" +
        "dateKind='" + dateKind + '\'' +
        ", dateName='" + dateName + '\'' +
        ", isHoliday=" + isHoliday +
        ", locDate='" + locDate + '\'' +
        ", createdAt=" + createdAt +
        '}';
  }
}
