package com.example.ZhangDT.bean.exam;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrangeRequest {

    private List<Integer> examIds;

    private List<Integer> majorIds;
}
