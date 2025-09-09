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

        return luminariaRepository.save(luminaria);
    }

    /**
     * Listar todas as luminárias
     */
    @Transactional(readOnly = true)
    public List<Luminaria> getAllLuminarias() {
        return luminariaRepository.findAll();
    }

    /**
     * Buscar luminárias por ambiente
     */
    @Transactional(readOnly = true)
    public List<Luminaria> getLuminariasByEnvironmentId(Long environmentId) {
        ambienteService.getById(environmentId); // Verificar se o ambiente existe
        return luminariaRepository.findByAmbienteIdOrderByNameAsc(environmentId);
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

        return luminariaRepository.save(luminaria);
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