package by.yarom.library.Controller;

import by.yarom.library.backup.Backup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@PropertySource("classpath:application.properties")
public class AdminController {

    @Value("${file.directory}")
    private String fileDirectory;

    private String nameSaveFile = "backup.sql";

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public String restoreBackup(@RequestParam("file") MultipartFile file,
                                HttpServletRequest request,
                                Model model) throws IOException, InterruptedException {

        if (file != null && !file.getOriginalFilename().isEmpty()) {
            String rootDirectory = request.getSession().getServletContext().getRealPath("/");
            Path path = Paths.get(rootDirectory + fileDirectory + nameSaveFile);
            File uploadDir = new File(rootDirectory + fileDirectory);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            file.transferTo(new File(path.toString()));
            Backup.backupRestoneSQL(model, path.toString());
        }else {
            throw new IOException("Ошибка файла");
        }
        return "/admin";
    }

    @RequestMapping(value = "/backup")
    public void backup(HttpServletRequest request,
                         HttpServletResponse response) throws IOException, InterruptedException {
        String rootDirectory = request.getSession().getServletContext().getRealPath("/");
        Path path = Paths.get(rootDirectory + fileDirectory + nameSaveFile);
        File uploadDir = new File(rootDirectory + fileDirectory);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        Backup.backupSQL(path.toString());

        if (Files.exists(path)) {
            response.setContentType("application/sql");
            response.addHeader("Content-Disposition", "attachment; filename="+ nameSaveFile);
            Files.copy(path, response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    @ExceptionHandler(value = InterruptedException.class)
    public ModelAndView handleIOException(InterruptedException exception) {
        ModelAndView modelAndView = new ModelAndView("/error");
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(value = IOException.class)
    public ModelAndView handleIOException(IOException exception) {
        ModelAndView modelAndView = new ModelAndView("/error");
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String admin(){
        return "/admin";
    }
}
