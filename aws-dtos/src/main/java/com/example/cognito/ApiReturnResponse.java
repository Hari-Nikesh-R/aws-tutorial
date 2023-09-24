/*
 * Copyright (C) 2023-2024 Kaytes Pvt Ltd. The right to copy, distribute, modify, or otherwise
 * make use of this software may be licensed only pursuant to the terms of an applicable Kaytes Pvt Ltd license agreement.
 */
package com.example.cognito;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The ApiReturnResponse class is used for the common response to be sent
 */
@Getter
@Setter
public class ApiReturnResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Boolean status;
	
	private String message;
	
	private Integer statusCode;
		
}
