package org.example.cloudstorage1.mapper;

import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.util.FolderPathUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileNodeMapper {
    public ResourceResponse toResponse(FileNode fileNode){
        return new ResourceResponse(
                FolderPathUtil.getParentPath(fileNode.getPath()),
                fileNode.isFile() ? fileNode.getName() : fileNode.getName() + "/",
                fileNode.getSize(),
                fileNode.getType()
        );
    }

    public List<ResourceResponse> toResponseList(List<FileNode> fileNodeList) {
        return fileNodeList.stream()
                .map(this::toResponse)
                .toList();
    }
}
