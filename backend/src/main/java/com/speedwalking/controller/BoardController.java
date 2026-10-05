package com.speedwalking.controller;

import com.speedwalking.dto.BoardSummaryDto;
import com.speedwalking.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<BoardSummaryDto> getSummary(@RequestParam(defaultValue = "1") Long competitionId) {
        return ResponseEntity.ok(boardService.getBoardSummary(competitionId));
    }
}
