package com.contafree.auth_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.contafree.auth_service.dto.UserResponse;
import com.contafree.auth_service.entity.User;

@Mapper(componentModel = "spring")
public interface userMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	User toUser (UserResponse userResponse);
	
	UserResponse toUserResponse ( User user);
}
