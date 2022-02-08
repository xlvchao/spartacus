package com.xlc.spartacus.resource.controller;

import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.resource.service.CosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * cos接口
 *
 * @author xlc, since 2021
 */
@RestController
@RequestMapping("/cos")
public class CosController {
	
	@Autowired
	private CosService cosService;


	@RequestMapping("/search")
	public BaseResponse search(@ModelAttribute BaseRequest baseRequest) {
		return cosService.search(baseRequest.getSearchText(), baseRequest.getCosType(), baseRequest.getRootDirPath(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping(value = "/batchMove")
	public BaseResponse batchMove(@ModelAttribute BaseRequest baseRequest) {
		return cosService.batchMove(baseRequest.getKeysStr(), baseRequest.getDestDirPath(), baseRequest.getEventId());
	}

	@RequestMapping(value = "/batchDelete")
	public BaseResponse batchDelete(@ModelAttribute BaseRequest baseRequest) {
		return cosService.batchDelete(baseRequest.getKeysStr(), baseRequest.getEventId());
	}

	@RequestMapping(value = "/batchDownload")
	public BaseResponse batchDownload(@ModelAttribute BaseRequest baseRequest) {
		return cosService.batchDownload(baseRequest.getKeysStr());
	}

	@RequestMapping(value = "/rename")
	public BaseResponse rename(@ModelAttribute BaseRequest baseRequest) {
		return cosService.rename(baseRequest.getKey(), baseRequest.getNewFileName(), baseRequest.getEventId());
	}

	@RequestMapping(value = "/download")
	public BaseResponse download(@ModelAttribute BaseRequest baseRequest) {
		return cosService.download(baseRequest.getKey());
	}

	@RequestMapping(value = "/listTags")
	public BaseResponse listTags() {
		return cosService.listTags();
	}

	@RequestMapping(value = "/webUploader", method = RequestMethod.POST/*, produces = "application/json;charset=utf8"*/)
	public BaseResponse webUploader(@RequestParam("file") MultipartFile file, String parentDirPath, Long parentId, String tags) throws IOException {
		return cosService.webUploader(parentDirPath, parentId, file.getOriginalFilename(), tags, file.getBytes());
	}
	
	@RequestMapping("/listObjects")
	public BaseResponse listObjects(@ModelAttribute BaseRequest baseRequest) {
		return cosService.listObjects(baseRequest.getIsRecursive(), baseRequest.getDirPath(), baseRequest.getCurrentPage(), baseRequest.getPageSize(), baseRequest.getTag());
	}
	
	@RequestMapping("/batchSetObjectAcl")
	public BaseResponse batchSetObjectAcl(@ModelAttribute BaseRequest baseRequest) {
		return cosService.batchSetObjectAcl(baseRequest.getKeysStr(), baseRequest.getAclFlag(), baseRequest.getEventId());
	}
	
	@RequestMapping("/deleteDirectory")
	public BaseResponse deleteDirectory(@ModelAttribute BaseRequest baseRequest) {
		return cosService.deleteDirectory(baseRequest.getTargetDirPath(), baseRequest.getEventId());
	}
	
	@RequestMapping("/createDirectory")
	public BaseResponse createDirectory(@ModelAttribute BaseRequest baseRequest) {
		return cosService.createDirectory(baseRequest.getParentDirPath(), baseRequest.getParentId(), baseRequest.getNewDirName());
	}
	
	@RequestMapping("/getDirectoryTree")
	public BaseResponse getDirectoryTree(@ModelAttribute BaseRequest baseRequest) {
		return cosService.getDirectoryTree(baseRequest.getRootDirPath());
	}
	
	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	public BaseResponse fileUpload(@RequestParam("file") MultipartFile file, String baseDirPath) throws IOException {
		return cosService.fileUpload(file.getOriginalFilename(), file.getBytes(), baseDirPath);
	}

	@RequestMapping("/loadHistoryLogos")
	public BaseResponse loadHistoryLogos() {
		return cosService.loadHistoryLogos();
	}
	
}
