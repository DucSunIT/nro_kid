/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.resources.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Văn Tuấn - 0337766460
 */
@AllArgsConstructor
@Builder
@Getter
public class ImageByName {

    private String filename;
    private int nFame;
}
