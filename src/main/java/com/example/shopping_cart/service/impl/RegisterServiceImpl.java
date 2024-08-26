package com.example.shopping_cart.service.impl;

import com.example.shopping_cart.model.Register;
import com.example.shopping_cart.repository.RegisterRepository;
import com.example.shopping_cart.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private RegisterRepository registerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Register saveRegister(Register register) {
        register.setRole("ROLE_USER");
        String password = passwordEncoder.encode(register.getPassword());
        register.setPassword(password);
        Register saveRegister = registerRepository.save(register);
        return saveRegister;
    }

    @Override
    public Register getByRegisterEmail(String email) {
        return registerRepository.findByEmail(email);
    }

    @Override
    public Register updateProfile(Register register, MultipartFile img) throws IOException {
        Register dbUser = registerRepository.findById(register.getId()).get();
        if(!img.isEmpty())
        {
            dbUser.setProfileImage(img.getOriginalFilename());
        }
        if(!ObjectUtils.isEmpty(dbUser))
        {
            // set lai du lieu cho no
            dbUser.setName(register.getName());
            dbUser.setMobilNumber(register.getMobilNumber());

            dbUser.setAddress(register.getAddress());
            dbUser.setCity(register.getCity());
            dbUser.setState(register.getState());
            dbUser.setPincode(register.getPincode());
            dbUser =registerRepository.save(dbUser);

        }
        if(!img.isEmpty())
        {
            File saveFile = new ClassPathResource("static/img").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                    + img.getOriginalFilename());

            // System.out.println(path);
            Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }



      return  dbUser;
    }

    @Override
    public Register saveAdmin(Register register) {
        register.setRole("ROLE_ADMIN");
        String password = passwordEncoder.encode(register.getPassword());
        register.setPassword(password);
        Register saveAdmin = registerRepository.save(register);
        return saveAdmin;
    }

    @Override
    public List<Register> getUsers(String role) {
       return registerRepository.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {
        Optional<Register> findByuser = registerRepository.findById(id);
        if (findByuser.isPresent()) {
            Register user = findByuser.get();
            user.setIsEnable(status);
            registerRepository.save(user);
            return true;
        }
        return false;
    }


}
