const fs = require('fs');
const path = require('path');


const tempRepository = (name) => {
    return `
    package src.repository;

import src.model.${name};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface I${name}Repository extends JpaRepository<${name}, UUID> {
}

    `
}



const createFile = (dir, templateFileName, template) =>
    fs.readdir('./model', function (err, files) {
        if (err) {
            console.log('Error getting directory information:', err);
        } else {
            files.forEach(function (file) {
                const name = file.split('.')[0]
                const fileName = templateFileName.replace("###", name)
                fs.writeFile(`./${dir}/${fileName}`, template(name), function (err) {
                    if (err) throw err;
                    console.log(`File ${fileName} created successfully.`);
                });
            });
        }
    });

const tempInputDto = (name) => `
package src.service.${name}.Dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ${name}InputDto {

}
`
const tempDto = (name) => `
package src.service.${name}.Dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Date;
import java.util.UUID;

import lombok.Data;

@Data
public class ${name}Dto extends ${name}UpdateDto {
    @JsonProperty(value = "name", required = true)
    public UUID Id;
    @JsonProperty(value = "name", required = true)
    public Date createAt ;
    @JsonProperty(value = "name", required = true)
    public Date updateAt ;
}

`
const tempCreateDto = (name) => `package src.service.${name}.Dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ${name}CreateDto {

}`
const tempUpdateDto = (name) => `
package src.service.${name}.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ${name}UpdateDto extends  ${name}CreateDto{
    @JsonProperty(value = "isDeleted")
    public Boolean isDeleted  = false;

}

`
const tempService = (name) => `

package src.service.${name};

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import src.model.${name};
import src.repository.I${name}Repository;
import src.service.${name}.Dtos.${name}CreateDto;
import src.service.${name}.Dtos.${name}Dto;
import src.service.${name}.Dtos.${name}UpdateDto;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ${name}Service {
    @Autowired
    private I${name}Repository ${name.toLowerCase()}Repository;
    @Autowired
    private ModelMapper toDto;

    @Async
    public CompletableFuture<List<${name}Dto>> getAll() {
        return CompletableFuture.completedFuture(
                (List<${name}Dto>) ${name.toLowerCase()}Repository.findAll().stream().map(
                        x -> toDto.map(x, ${name}Dto.class)
                ).collect(Collectors.toList()));
    }

    @Async
    public CompletableFuture<${name}Dto> getOne(UUID id) {
        return CompletableFuture.completedFuture(toDto.map(${name.toLowerCase()}Repository.findById(id), ${name}Dto.class));
    }

    @Async
    public CompletableFuture<${name}Dto> create(${name}CreateDto input) {
        ${name} ${name.toLowerCase()} = ${name.toLowerCase()}Repository.save(toDto.map(input, ${name}.class));
        return CompletableFuture.completedFuture(toDto.map(${name.toLowerCase()}Repository.save(${name.toLowerCase()}), ${name}Dto.class));
    }

    @Async
    public CompletableFuture<${name}Dto> update(UUID id, ${name}UpdateDto ${name.toLowerCase()}) {
        ${name} existing${name} = ${name.toLowerCase()}Repository.findById(id).orElse(null);
        if (existing${name} == null)
            throw new ResponseStatusException(NOT_FOUND, "Unable to find user level!");
        return CompletableFuture.completedFuture(toDto.map(${name.toLowerCase()}Repository.save(toDto.map(${name.toLowerCase()}, ${name}.class)), ${name}Dto.class));
    }

    @Async
    public CompletableFuture<Void> remove(UUID id) {
        ${name} existing${name} = ${name.toLowerCase()}Repository.findById(id).orElse(null);
        if (existing${name} == null)
            throw new ResponseStatusException(NOT_FOUND, "Unable to find user level!");
        existing${name}.setDeleted(true);
        ${name.toLowerCase()}Repository.save(toDto.map(existing${name}, ${name}.class));
        return CompletableFuture.completedFuture(null);
    }
}

`
tempController = (name) => `
package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import src.config.annotation.ApiPrefixController;
import src.service.${name}.Dtos.${name}CreateDto;
import src.service.${name}.Dtos.${name}Dto;
import src.service.${name}.Dtos.${name}UpdateDto;
import src.service.${name}.${name}Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@ApiPrefixController(value = "/${name.toLowerCase()}s")
public class ${name}Controller {
    @Autowired
    private ${name}Service ${name.toLowerCase()}Service;


    @GetMapping( "/{id}")
//    @Tag(name = "${name.toLowerCase()}s", description = "Operations related to ${name.toLowerCase()}s")
//    @Operation(summary = "Hello API")
    public CompletableFuture<${name}Dto> findOneById(@PathVariable UUID id) {
        return ${name.toLowerCase()}Service.getOne(id);
    }

    @GetMapping()
//    @Tag(name = "${name.toLowerCase()}s", description = "Operations related to ${name.toLowerCase()}s")
//    @Operation(summary = "Hello API")
    public CompletableFuture<List<${name}Dto>> findAll() {
       return ${name.toLowerCase()}Service.getAll();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    @Tag(name = "${name.toLowerCase()}s", description = "Operations related to ${name.toLowerCase()}s")
//    @Operation(summary = "Hello API")
    public CompletableFuture<${name}Dto> create(@RequestBody ${name}CreateDto input) {
        return ${name.toLowerCase()}Service.create(input);
    }

    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @Tag(name = "${name.toLowerCase()}s", description = "Operations related to ${name.toLowerCase()}s")
//    @Operation(summary = "Hello API")
    public CompletableFuture<${name}Dto> update(@PathVariable UUID id, ${name}UpdateDto ${name.toLowerCase()}) {
        return ${name.toLowerCase()}Service.update(id, ${name.toLowerCase()});
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @Tag(name = "${name.toLowerCase()}s", description = "Operations related to ${name.toLowerCase()}s")
//    @Operation(summary = "Remove")
    public CompletableFuture<Void> remove(@PathVariable UUID id) {
        return ${name.toLowerCase()}Service.remove(id);
    }
}
`
const createService = (templateFileName) =>
    fs.readdir('./model', function (err, files) {
        if (err) {
            console.log('Error getting directory information:', err);
        } else {
            files.forEach(function (file) {
                const name = file.split('.')[0]
                const fileName = templateFileName.replace("###", name)
                fs.mkdirSync(`./service/${name}`);
                fs.mkdirSync(`./service/${name}/Dtos`);
                fs.writeFile(`./service/${name}/Dtos/${name}Dto.java`, tempDto(name), function (err) {
                    if (err) throw err;
                    console.log(`File ${fileName} created successfully.`);
                });
                fs.writeFile(`./service/${name}/Dtos/${name}InputDto.java`, tempInputDto(name), function (err) {
                    if (err) throw err;
                    console.log(`File ${fileName} created successfully.`);
                });
                fs.writeFile(`./service/${name}/Dtos/${name}CreateDto.java`, tempCreateDto(name), function (err) {
                    if (err) throw err;
                    console.log(`File ${fileName} created successfully.`);
                });
                fs.writeFile(`./service/${name}/Dtos/${name}UpdateDto.java`, tempUpdateDto(name), function (err) {
                    if (err) throw err;
                    console.log(`File ${fileName} created successfully.`);
                });
                fs.writeFile(`./service/${name}/${name}Service.java`, tempService(name), function (err) {
                    if (err) throw err;
                    console.log(`File ${fileName} created successfully.`);
                });
            });
        }
    });


const tempIService = (name) => `
package src.service.${name};

import src.service.IService;
import src.service.${name}.Dtos.${name}CreateDto;
import src.service.${name}.Dtos.${name}Dto;
import src.service.${name}.Dtos.${name}UpdateDto;

public interface I${name}Service extends IService<${name}Dto, ${name}CreateDto, ${name}UpdateDto, FindManyArgs> {
}
    `


// // create file in repository
// createFile("repository", "I###Repository.java", tempRepository)
// // create file in dto
// create file in service
// createService("###Service.java")
// createFile("controller", "###Controller.java", tempController)

const createFileI = (dir, templateFileName, template) =>
    fs.readdir('./model', function (err, files) {
        if (err) {
            console.log('Error getting directory information:', err);
        } else {
            files.forEach(function (file) {
                const name = file.split('.')[0]
                const fileName = templateFileName.replace("###", name)
                fs.writeFile(`./${dir}/${name}/${fileName}`, template(name), function (err) {
                    if (err) throw err;
                    console.log(`File ${fileName} created successfully.`);
                });
            });
        }
    });

createFileI("service", "I###Service.java", tempIService)

