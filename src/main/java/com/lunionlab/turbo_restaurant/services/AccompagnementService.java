package com.lunionlab.turbo_restaurant.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.lunionlab.turbo_restaurant.Enums.DeletionEnum;
import com.lunionlab.turbo_restaurant.form.CreateAccompagnementForm;
import com.lunionlab.turbo_restaurant.form.UpdateAccompagnementForm;
import com.lunionlab.turbo_restaurant.model.AccompagnementModel;
import com.lunionlab.turbo_restaurant.model.PlatModel;
import com.lunionlab.turbo_restaurant.model.RestaurantModel;
import com.lunionlab.turbo_restaurant.repository.AccompagnementRepo;
import com.lunionlab.turbo_restaurant.repository.PlatRepository;
import com.lunionlab.turbo_restaurant.utilities.Report;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccompagnementService {
    @Autowired
    AccompagnementRepo accompagnementRepo;
    @Autowired
    PlatRepository platRepository;

    @Autowired
    GenericService genericService;

    public Object createAccompagnement(@Valid CreateAccompagnementForm form, BindingResult result) {
        if (result.hasErrors()) {
            log.error("mauvais format des données");
            return ResponseEntity.badRequest().body(Report.getErrors(result));
        }
        Optional<PlatModel> platOpt = platRepository.findFirstByIdAndDeleted(form.getPlatId(), DeletionEnum.NO);
        if (platOpt.isEmpty()) {
            log.error("aucun plat trouvé");
            return ResponseEntity.badRequest().body(Report.message("message", "aucun plat trouvé"));
        }
        Boolean isExist = accompagnementRepo.existsByLibelleAndPlatModelAndDeleted(form.getLibelle(), platOpt.get(),
                DeletionEnum.NO);
        if (isExist) {
            log.error("cet accompagnement existe déjà");
            return ResponseEntity.badRequest().body(Report.message("message", "cet accompagnement existe déjà"));
        }
        AccompagnementModel accompagnementModel = new AccompagnementModel(form.getLibelle(), form.getPrice(),
                platOpt.get());
        accompagnementModel = accompagnementRepo.save(accompagnementModel);
        log.info("accompagnement créé avec succès");
        return ResponseEntity.ok(accompagnementModel);
    }

    public Object getAccompagnementForPlat(UUID platId) {
        RestaurantModel restaurant = genericService.getAuthUser().getRestaurant();
        if (restaurant == null) {
            log.error("restaurant not found");
            return ResponseEntity.badRequest().body(Report.notFound("restaurant not found"));
        }
        Optional<PlatModel> platOpt = platRepository.findFirstByIdAndRestaurantAndDeleted(platId, restaurant,
                DeletionEnum.NO);
        if (platOpt.isEmpty()) {
            log.error("this plat not found");
            return ResponseEntity.badRequest().body(Report.message("message", "this plat not found"));
        }
        List<AccompagnementModel> accompagnements = accompagnementRepo.findByPlatModelAndDeleted(platOpt.get(),
                DeletionEnum.NO);
        log.info("get accompagnement list for a plat");
        return ResponseEntity.ok(accompagnements);
    }

    public Object detailAccompagnement(UUID accompagnementId) {
        Optional<AccompagnementModel> accompagnementOpt = accompagnementRepo.findFirstByIdAndDeleted(accompagnementId,
                DeletionEnum.NO);
        if (accompagnementOpt.isEmpty()) {
            log.error("this accompagnement not found");
            return ResponseEntity.badRequest().body(Report.message("message", "this accompagnement not found"));
        }
        log.info("get accompagnement");
        return ResponseEntity.ok(accompagnementOpt.get());
    }

    public Object updateAccompagnement(UUID accompagnementId, UpdateAccompagnementForm form) {
        Optional<AccompagnementModel> accompagnementOpt = accompagnementRepo.findFirstByIdAndDeleted(accompagnementId,
                DeletionEnum.NO);
        if (accompagnementOpt.isEmpty()) {
            log.error("this accompagnement not found");
            return ResponseEntity.badRequest().body(Report.message("message", "this accompagnement not found"));
        }
        AccompagnementModel accompagnement = accompagnementOpt.get();
        if (!form.getLibelle().isEmpty() && form.getLibelle() != null) {
            accompagnement.setLibelle(form.getLibelle());
        }

        if (form.getPrice() != null) {
            accompagnement.setPrice(form.getPrice());
        }

        accompagnement = accompagnementRepo.save(accompagnement);
        log.info("update accompagnement");
        return ResponseEntity.ok(accompagnement);
    }

}
