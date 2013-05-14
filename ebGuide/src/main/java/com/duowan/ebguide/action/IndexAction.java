/**
 * 
 */
package com.duowan.ebguide.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.duowan.ebguide.dao.SnatchProductMapper;
import com.duowan.ebguide.vo.JsonResponse;
import com.duowan.ebguide.vo.JsonResponse.BaseJsonResponse;


/**
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * 2013-4-25 
 */

@Controller
@RequestMapping("/index")
public class IndexAction {
	
	@Autowired
    private SnatchProductMapper snatchProductMapper;
    
    public void setSnatchProductMapper(SnatchProductMapper snatchProductMapper) {
		this.snatchProductMapper = snatchProductMapper;
	}

	@RequestMapping("/list")
    public String uploadUI(Model model,
    		@RequestParam(required=false) String website){
		model.addAttribute("products", snatchProductMapper.selectByLimit(0, 30, website));
        return "index/index";
    }
    
   
    
}
