package com.ims.ws.products.rest;

import java.util.Date;

public record ErrorMessage (Date timestamp, String message, String details) {}
