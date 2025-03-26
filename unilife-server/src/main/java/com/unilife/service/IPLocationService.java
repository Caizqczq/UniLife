package com.unilife.service;
import com.unilife.model.dto.LoginDTO;

import javax.servlet.http.HttpServletRequest;

public interface IPLocationService {
    public String getIPLocation(String ip);
    public String getClientIP(HttpServletRequest request);
}
