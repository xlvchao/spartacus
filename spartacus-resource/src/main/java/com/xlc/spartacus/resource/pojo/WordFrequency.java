package com.xlc.spartacus.resource.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 词频
 *
 * @author xlc, since 2021
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordFrequency {
    String text;
    Integer weight;
}
