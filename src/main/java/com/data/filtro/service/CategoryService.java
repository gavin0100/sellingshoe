package com.data.filtro.service;

import com.data.filtro.model.Account;
import com.data.filtro.model.Category;
import com.data.filtro.model.Material;
import com.data.filtro.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipInputStream;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;


    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(int id) {
        return categoryRepository.findById(id);
    }

    public Page<Category> getAllPaging(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public void create(Category category) {
        categoryRepository.save(category);
    }

    public Category createCategory(Category category) {
        Category cate = categoryRepository.save(category);
        return cate;
    }

    public void update(Category category) {
        Category newCategory = getCategoryById(category.getId());
        newCategory.setCategoryName(category.getCategoryName());
        newCategory.setStatus(category.getStatus());
        categoryRepository.save(newCategory);
    }

    public Category updateCategory(int id, Category category) {
        System.out.println(category.getId() != null ? category.getId() : "null");
        Category newCategory = getCategoryById(id);
        if (newCategory != null) {
            newCategory.setCategoryName(category.getCategoryName());
            newCategory.setStatus(category.getStatus());
            categoryRepository.save(newCategory);
        }
        return newCategory;
    }

    public List<Material> getListMaterials(){
         return categoryRepository.getMaterialList();
    }

    @Transactional
    public void delete(int id) {
        categoryRepository.deleteById(id);
    }
    public List<Category> getActiveCategory(int status){
        return categoryRepository.activeCategories(status);
    }
    public List<Category> get5Categories() {
        return categoryRepository.find5Categories();
    }

    public boolean importCategory(MultipartFile file) throws IOException {
        if (checkExcelFormat(file)){
            Map<String, List<?>> result = toSyllabus(file.getInputStream());
            System.out.println("Sau khi chuyen");
            List<Category> categoryList = (List<Category>) result.get("category");

            categoryList.forEach(category -> {
                Category category1 = new Category();
                category1.setCategoryName(category.getCategoryName());
                category1.setStatus(category.getStatus());
                categoryRepository.save(category1);
            });


            return true;
        }
        else return false;

    }



    public boolean checkExcelFormat(MultipartFile file){
        String contentType = file.getContentType();
        if (contentType == null) throw new AssertionError();
        return contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }



    public Map<String, List<?>> toSyllabus(InputStream inputStream){
        List<Category> categoryList = new ArrayList<>();
        System.out.println("truoc try");
        try{
            System.out.println("tao workbook");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            System.out.println("truy cap sheet");
            XSSFSheet sheet = workbook.getSheet("categories");
            System.out.println("sau khi truy cap sheet");
            int rowNumber = 0;
            for (Row row : sheet) {
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cells = row.iterator();
                int cid = 0;
                Category categoryImport = new Category();
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    switch (cid) {
                        case 0 -> categoryImport.setCategoryName(cell.getStringCellValue());
                        case 1 -> categoryImport.setStatus((int) cell.getNumericCellValue());
                        default -> {
                        }
                    }
                    cid++;
                }
                System.out.println(categoryImport.getCategoryName());
                categoryList.add(categoryImport);
            }

        } catch (Exception e){
            throw new RuntimeException("Error when convert file csv!" + e);
        }
        System.out.println("sau try");
        HashMap<String, List<?>> map = new HashMap<>();
        map.put("category", categoryList);
        return map;
    }

}
