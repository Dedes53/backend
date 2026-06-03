package federicolepore.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import federicolepore.backend.exceptions.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadAvatar(MultipartFile file, String publicId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File immagine mancante");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "skillswap/avatars",
                            "public_id", publicId,
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );

            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                throw new BadRequestException("Upload fallito: URL non disponibile");
            }

            return secureUrl.toString();
        } catch (IOException e) {
            throw new BadRequestException("Errore durante upload immagine");
        }
    }
}