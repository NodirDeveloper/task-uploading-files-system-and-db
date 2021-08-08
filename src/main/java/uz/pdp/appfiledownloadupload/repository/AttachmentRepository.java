package uz.pdp.appfiledownloadupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appfiledownloadupload.entitiy.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment,Integer> {


}
