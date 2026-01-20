package org.example.cloudstorage1.service.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.InvalidPathException;
import org.example.cloudstorage1.exception.ResourceNotFoundException;
import org.example.cloudstorage1.repository.FileNodeRepository;
import org.example.cloudstorage1.service.DirectoryService;
import org.example.cloudstorage1.service.FileNodeService;
import org.example.cloudstorage1.util.ResourcePathUtil;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoveService {
    private final DirectoryService directoryService;
    private final FileNodeService fileNodeService;
    private final FileNodeRepository fileNodeRepository;

    public FileNode moveResource(User user, FileNode fileNode, String to) {
        String targetParentPath = ResourcePathUtil.getParentPath(to);
        if(!fileNode.isFile() && targetParentPath.startsWith(fileNode.getPath())){
            throw new InvalidPathException("Cannot move folder into its own subfolder");
        }
        String newName = ResourcePathUtil.getName(to);
        String newPath = to;
        Long newParentId = targetParentPath.isEmpty() ? null :
                fileNodeRepository.findByOwnerIdAndPath(user.getId(), targetParentPath)
                .orElseThrow(() -> new ResourceNotFoundException("Resource Not found" + targetParentPath))
                .getId();
        fileNode.setName(newName);
        fileNode.setPath(newPath);
        fileNode.setParentId(newParentId);
        fileNodeRepository.save(fileNode);
        fileNodeService.updateAllChildren(fileNode);
        return fileNode;
    }
}


