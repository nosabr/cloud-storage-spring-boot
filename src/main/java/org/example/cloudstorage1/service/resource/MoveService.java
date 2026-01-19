package org.example.cloudstorage1.service.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.InvalidPathException;
import org.example.cloudstorage1.service.DirectoryService;
import org.example.cloudstorage1.util.ResourcePathUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoveService {
    private final DirectoryService directoryService;

    public ResourceResponse moveResource(User user, FileNode fileNode, String to) {
        String fromParentPath = ResourcePathUtil.getParentPath(fileNode.getPath());
        String movingParentPath = ResourcePathUtil.getParentPath(to);
        Long movingParentId = directoryService.getParentId(user.getId(), toParentPath);
        if(movingParentPath.startsWith(fromParentPath)){
            throw new InvalidPathException("Cannot move folder into its own subfolder");
        }
        return null;
    }


}
