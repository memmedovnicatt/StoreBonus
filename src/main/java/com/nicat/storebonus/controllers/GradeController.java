package com.nicat.storebonus.controllers;

import com.nicat.storebonus.dtos.request.GradeRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.services.GradeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GradeController {
    GradeService gradeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createGrade(@Valid @RequestBody GradeRequest gradeRequest) {
        gradeService.create(gradeRequest);
        return ResponseEntity.ok(ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE));
    }
}