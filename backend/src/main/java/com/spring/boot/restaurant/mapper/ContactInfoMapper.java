package com.spring.boot.restaurant.mapper;

import com.spring.boot.restaurant.dto.ContactInfoDto;
import com.spring.boot.restaurant.model.ContactInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactInfoMapper {
    ContactInfoDto toDto(ContactInfo entity);
    ContactInfo toEntity(ContactInfoDto dto);
    List<ContactInfoDto> toDtoList(List<ContactInfo> entities);
    List<ContactInfo> toEntityList(List<ContactInfoDto> dtos);
}
