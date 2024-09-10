package com.data.filtro.handler;


//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class FilterExceptionHandler extends Author {
public class FilterExceptionHandler  {
//    private final Logger logger = LoggerFactory.getLogger(FilterExceptionHandler.class);
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try{
//            filterChain.doFilter(request, response);
//        }catch (ExpiredJwtException ee){
//            handleExpiredJwtException(request,response);
//        }catch(MalformedJwtException me){
//            handleMalformedJwtException(request,response);
//        }catch (SignatureException se){
//            handleSignatureException(request,response);
//        }catch (AccessDeniedException ac){
//            System.out.println("AccessDeniedException trong FilterExceptionHanlder");
//            response.sendRedirect("/");
//        }catch (Exception ex){
//            System.out.println("Exception trong FilterExceptionHanlder");
//            response.sendRedirect("/access-denied");
//        }
//    }
//
//    public void handleExpiredJwtException(HttpServletRequest request,HttpServletResponse response) throws IOException {
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .statusCode(HttpStatus.UNAUTHORIZED.value())
//                .message("Token đã hết hạn")
//                .description(request.getRequestURL().toString())
//                .timestamp(new Date())
//                .build();
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(convertToJSON(errorResponse));
//        logger.error(errorResponse.getMessage());
//    }
//
//    public void handleMalformedJwtException(HttpServletRequest request,HttpServletResponse response) throws IOException {
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .statusCode(HttpStatus.FORBIDDEN.value())
//                .message("Giá trị token đã bị thay đổi")
//                .description(request.getRequestURL().toString())
//                .timestamp(new Date())
//                .build();
//        response.setStatus(HttpStatus.FORBIDDEN.value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(convertToJSON(errorResponse));
//        logger.error(errorResponse.getMessage());
//    }
//
//    public void handleSignatureException(HttpServletRequest request,HttpServletResponse response) throws IOException {
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .statusCode(HttpStatus.FORBIDDEN.value())
//                .message("Signature của token không khớp với hệ thống")
//                .description(request.getRequestURL().toString())
//                .timestamp(new Date())
//                .build();
//        response.setStatus(HttpStatus.FORBIDDEN.value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(convertToJSON(errorResponse));
//        logger.error(errorResponse.getMessage());
//    }
//
//    public String convertToJSON(Object object) throws JsonProcessingException {
//        if(object == null){
//            return null;
//        }
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.writeValueAsString(object);
//    }
}
