package com.scenario.automation.service;

import com.scenario.automation.model.Luminaria;
import com.scenario.automation.model.Ambiente;
import com.scenario.automation.repository.LuminariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LuminariaService {

    @Autowired
    private LuminariaRepository luminariaRepository;

    @Autowired
    private AmbienteService ambienteService;

    /**
     * Criar nova luminária
     */
    public Luminaria createLuminaria(Luminaria luminaria) {
        // Verificar se o ambiente existe
        Ambiente ambiente = null;
        
        if (luminaria.getAmbiente() != null && luminaria.getAmbiente().getId() != null) {
            ambiente = ambienteService.getById(luminaria.getAmbiente().getId());
        } else if (luminaria.getEnvironmentId() != null) {
            ambiente = ambienteService.getById(luminaria.getEnvironmentId());
        } else {
            throw new RuntimeException("É obrigatório informar o ambiente da luminária");
        }
        
        luminaria.setAmbiente(ambiente);

        // Verificar se já existe luminária com o mesmo nome no ambiente
        if (luminariaRepository.existsByNameIgnoreCaseAndAmbienteId(luminaria.getName(), ambiente.getId())) {
            throw new RuntimeException("Já existe uma luminária com o nome '" + luminaria.getName() + 
                                     "' no ambiente '" + ambiente.getName() + "'");
        }

        Luminaria savedLuminaria = luminariaRepository.save(luminaria);
        // Preencher o campo environmentId para o front-end
        savedLuminaria.setEnvironmentId(ambiente.getId());
        return savedLuminaria;
    }

    /**
     * Listar todas as luminárias
     */
    @Transactional(readOnly = true)
    public List<Luminaria> getAllLuminarias() {
        List<Luminaria> luminarias = luminariaRepository.findAll();
        // Preencher o campo environmentId para o front-end
        for (Luminaria luminaria : luminarias) {
            if (luminaria.getAmbiente() != null) {
                luminaria.setEnvironmentId(luminaria.getAmbiente().getId());
            }
        }
        return luminarias;
    }

    /**
     * Buscar luminárias por ambiente
     */
    @Transactional(readOnly = true)
    public List<Luminaria> getLuminariasByEnvironmentId(Long environmentId) {
        ambienteService.getById(environmentId); // Verificar se o ambiente existe
        List<Luminaria> luminarias = luminariaRepository.findByAmbienteIdOrderByNameAsc(environmentId);
        // Preencher o campo environmentId para o front-end
        for (Luminaria luminaria : luminarias) {
            luminaria.setEnvironmentId(environmentId);
        }
        return luminarias;
    }

    /**
     * Buscar luminária por ID
     */
    @Transactional(readOnly = true)
    public Optional<Luminaria> getLuminariaById(Long id) {
        return luminariaRepository.findById(id);
    }

    /**
     * Atualizar luminária
     */
    public Luminaria updateLuminaria(Long id, Luminaria luminariaAtualizada) {
        Luminaria luminaria = luminariaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Luminária não encontrada com ID: " + id));

        // Atualizar apenas o nome
        luminaria.setName(luminariaAtualizada.getName());

        // Verificar se o nome não conflita com outra luminária no mesmo ambiente
        if (luminariaRepository.existsByNameIgnoreCaseAndAmbienteIdAndIdNot(
                luminariaAtualizada.getName(), luminaria.getAmbiente().getId(), id)) {
            throw new RuntimeException("Já existe uma luminária com o nome '" + luminariaAtualizada.getName() + 
                                     "' no ambiente '" + luminaria.getAmbiente().getName() + "'");
        }

        Luminaria savedLuminaria = luminariaRepository.save(luminaria);
        // Preencher o campo environmentId para o front-end
        savedLuminaria.setEnvironmentId(luminaria.getAmbiente().getId());
        return savedLuminaria;
    }

    /**
     * Deletar luminária
     */
    public void deleteLuminaria(Long id) {
        if (!luminariaRepository.existsById(id)) {
            throw new RuntimeException("Luminária não encontrada com ID: " + id);
        }
        luminariaRepository.deleteById(id);
    }
}