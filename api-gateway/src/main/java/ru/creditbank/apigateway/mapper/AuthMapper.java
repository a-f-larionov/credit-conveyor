package ru.creditbank.apigateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.creditbank.apigateway.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;
import ru.creditbank.apigateway.dto.rs.UserInfoRsDto;
import ru.creditbank.apigateway.entitiy.UserEntity;
import ru.creditbank.common.library.enums.UserRole;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "token", source = "token")
    LoginRsDto toLoginRsDto(String token);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "email", source = "rqDto.email")
    @Mapping(target = "firstName", source = "rqDto.fullName.firstName")
    @Mapping(target = "middleName", source = "rqDto.fullName.middleName")
    @Mapping(target = "lastName", source = "rqDto.fullName.lastName")
    UserEntity mapRegisterRqDtoToUserEntity(RegisterRqDto rqDto, Set<UserRole> roles);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName.firstName", source = "userEntity.firstName")
    @Mapping(target = "fullName.middleName", source = "userEntity.middleName")
    @Mapping(target = "fullName.lastName", source = "userEntity.lastName")
    UserInfoRsDto mapUserToUserInfoRsDto(UserEntity userEntity);
}
