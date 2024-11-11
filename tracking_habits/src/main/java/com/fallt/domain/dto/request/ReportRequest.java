package com.fallt.domain.dto.request;

import com.fallt.util.Constant;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequest {

    private Long habitId;
    private LocalDate start;
    private LocalDate end;

    @AssertTrue(message = Constant.REPORT_PERIOD_MESSAGE)
    public boolean isCorrectPeriod(){
        return start != null && end != null && !start.isAfter(end);
    }

}
