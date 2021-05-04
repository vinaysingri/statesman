package io.appform.statesman.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageConfig {
    @NotNull
    String messageId;

    @NotNull
    String messageBody;
}
