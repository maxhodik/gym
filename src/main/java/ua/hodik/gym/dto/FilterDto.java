package ua.hodik.gym.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FilterDto<T> {
    private String column;
    private List<T> values;
    private Operation operations;
}