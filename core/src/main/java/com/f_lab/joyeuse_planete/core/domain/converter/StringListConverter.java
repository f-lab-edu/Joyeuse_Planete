package com.f_lab.joyeuse_planete.core.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

  private static final String SPLIT_CHAR = ";";

  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    return (attribute != null) ? String.join(SPLIT_CHAR, attribute) : "";
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    return (!ObjectUtils.isEmpty(dbData)) ? Arrays.asList(dbData.split(SPLIT_CHAR)) : emptyList();
  }
}
