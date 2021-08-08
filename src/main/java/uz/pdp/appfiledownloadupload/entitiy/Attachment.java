package uz.pdp.appfiledownloadupload.entitiy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fileOriginalName;//pdp.jpg,  inn.pdf

    private long size;// 204800

    private String contentType;  //application.pdf\\image/png

    //bu file systemga saqlaganda kerak boladi
    private String name;// papkani ichidan topish uchun





}
