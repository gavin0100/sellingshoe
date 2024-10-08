package com.data.filtro.repository;

import com.data.filtro.model.Category;
import com.data.filtro.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("select c from Material c order by c.materialName")
    List<Material> getMaterialList();

    @Query("select c from Category c order by c.id limit 5")
    List<Category> find5Categories();
    

    @Query("select c from Category c where c.id =:id")
    Category findById(@Param("id") int id);

    @Modifying
    @Query("update Category c set c.status = 0 where c.id =:id")
    void deleteById(@Param("id") int id);

    @Query("select a from Category a where a.status = :status")
    List<Category> activeCategories(@Param("status") int status);

    Page<Category> findAll(Pageable pageable);

}
