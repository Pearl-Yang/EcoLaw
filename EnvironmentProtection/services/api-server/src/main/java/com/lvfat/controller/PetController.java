package com.lvfat.controller;

import com.lvfat.common.result.Result;
import com.lvfat.dto.*;
import com.lvfat.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 宠物养成 Controller
 */
@Tag(name = "宠物养成")
@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @Operation(summary = "获取宠物信息")
    @GetMapping("/info/{userId}")
    public Result<PetInfoDTO> getPetInfo(
            @Parameter(description = "用户ID") @PathVariable String userId
    ) {
        return Result.success(petService.getPetInfo(userId));
    }

    @Operation(summary = "创建宠物")
    @PostMapping("/create")
    public Result<PetInfoDTO> createPet(@RequestBody CreatePetDTO dto) {
        return Result.success(petService.createPet(dto));
    }

    @Operation(summary = "更新宠物位置")
    @PostMapping("/position")
    public Result<Void> updatePosition(@RequestBody UpdatePositionDTO dto) {
        petService.updatePosition(dto);
        return Result.success("位置更新成功");
    }

    @Operation(summary = "完成互动")
    @PostMapping("/interaction")
    public Result<InteractionResultDTO> completeInteraction(@RequestBody CompleteInteractionDTO dto) {
        return Result.success(petService.completeInteraction(dto));
    }

    @Operation(summary = "获取场景信息")
    @GetMapping("/scene/{sceneId}")
    public Result<Object> getScene(
            @Parameter(description = "场景ID") @PathVariable Integer sceneId
    ) {
        return Result.success(petService.getScene(sceneId));
    }
}
