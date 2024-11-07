package com.tien.file.mapper;

import com.tien.file.dto.response.FileResponse;
import com.tien.file.entity.File;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

      FileResponse toFileUploadResponse(File file);

}