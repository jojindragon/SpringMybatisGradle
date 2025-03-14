package data.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import data.dto.SawonDto;
import data.service.SawonService;
import lombok.RequiredArgsConstructor;
import naver.storage.NcpObjectStorageService;

@Controller
@RequiredArgsConstructor
public class SawonController {
	final SawonService sawonService;
	final NcpObjectStorageService storageService;
	
	private String imagePath = "https://kr.object.ncloudstorage.com/bitcamp-bucket-139/sawon/";
	private String opPath = "https://radbwaqf8739.edge.naverncp.com/DaFq6foQou/sawon/";
	private String bucketName = "bitcamp-bucket-139";
	
	@GetMapping("/")
	public String mainPage() {
		return "sawon/mainpage";
	}
	
	@GetMapping("/list")
	public String sawonList(Model model) {
		List<SawonDto> list = sawonService.getSelectAllSawon();
		model.addAttribute("list", list);
		model.addAttribute("totalCnt", list.size());
		model.addAttribute("opPath", opPath);
		return "sawon/sawonlist";
	}
	
	@GetMapping("/form")
	public String sawonForm() {
		return "sawon/sawonform";
	}
	
	@PostMapping("/insert")
	public String sawonInsert(@ModelAttribute SawonDto dto,
			@RequestParam("upload") MultipartFile upload) {
		
		if(upload.getOriginalFilename().equals("")) {
			dto.setPhoto(null);
		} else {
			String photo = storageService.uploadFile(bucketName, "sawon", upload);
			dto.setPhoto(photo);
		}
		
		sawonService.insertSawon(dto);
		
		return "redirect:./list";
	}
	
	@GetMapping("/delete")
	public String sawonDelete(@RequestParam(value = "num") int num) {
		SawonDto dto = sawonService.getSawon(num);
		if(dto.getPhoto() != null) {
			storageService.deleteFile(bucketName, "sawon", dto.getPhoto());
		}
		
		sawonService.deleteSawon(num);
		
		return "redirect:./list";
	}
	
	@GetMapping("/detail")
	public String sawonDetail(Model model,
			@RequestParam(value = "num") int num) {
		SawonDto dto = sawonService.getSawon(num);
		model.addAttribute("dto", dto);
		model.addAttribute("imagePath", imagePath);
		
		return "sawon/sawondetail";
	}
	
	@GetMapping("/updateform")
	public String updateform(Model model,
			@RequestParam(value = "num") int num) {
		SawonDto dto = sawonService.getSawon(num);
		model.addAttribute("dto", dto);
		model.addAttribute("imagePath", imagePath);
		
		return "sawon/updateform";
	}
	
	@PostMapping("/update")
	public String updateSawon(@ModelAttribute SawonDto dto,
			@RequestParam("upload") MultipartFile upload) {
		SawonDto old = sawonService.getSawon(dto.getNum());
		
		if(!upload.getOriginalFilename().equals("")) {
			storageService.deleteFile(bucketName, "sawon", old.getPhoto());
			String photo = storageService.uploadFile(bucketName, "sawon", upload);
			dto.setPhoto(photo);
		} else {
			dto.setPhoto(old.getPhoto());
		}
		
		sawonService.updateSawon(dto);
		
		return "redirect:./detail?num="+dto.getNum();
	}
	
}
