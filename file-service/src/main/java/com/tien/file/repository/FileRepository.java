package com.tien.file.repository;

import com.tien.file.entity.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String> {

      void deleteByName(String fileName);

      Optional<File> findByName(String fileName);

}