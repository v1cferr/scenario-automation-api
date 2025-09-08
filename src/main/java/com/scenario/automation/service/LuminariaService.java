package com.scenario.automation.service;

import com.scenario.automation.model.Luminaria;
import com.scenario.automation.model.Ambiente;
import com.scenario.automation.repository.LuminariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        Ambiente ambiente = ambienteService.getById(luminaria.getAmbiente().getId());
        luminaria.setAmbiente(ambiente);

        // Verificar se já existe luminária com o mesmo nome no ambiente
        if (luminariaRepository.existsByNameIgnoreCaseAndAmbienteId(luminaria.getName(), ambiente.getId())) {
            throw new RuntimeException("Já existe uma luminária com o nome '" + luminaria.getName() + 
                                     "' no ambiente '" + ambiente.getName() + "'");
        }

        return luminariaRepository.save(luminaria);
    }

    /**
     * Buscar luminária por ID
     */
    @Transactional(readOnly = true)
    public Optional<Luminaria> findById(Long id) {
        return luminariaRepository.findById(id);
    }

    /**
     * Buscar luminária por ID (com exceção se não encontrar)
     */
    @Transactional(readOnly = true)
    public Luminaria getById(Long id) {
        return luminariaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Luminária não encontrada com ID: " + id));
    }

    /**
     * Listar todas as luminárias
     */
    @Transactional(readOnly = true)
    public List<Luminaria> findAll() {
        return luminariaRepository.findAll();
    }

    /**
     * Listar luminárias com paginação
     */
    @Transactional(readOnly = true)
    public Page<Luminaria> findAll(Pageable pageable) {
        return luminariaRepository.findAll(pageable);
    }

    /**
     * Buscar luminárias por ambiente
     */
    @Transactional(readOnly = true)
    public List<Luminaria> findByAmbienteId(Long ambienteId) {
        // Verificar se o ambiente existe
        ambienteService.getById(ambienteId);
        return luminariaRepository.findByAmbienteIdOrderByNameAsc(ambienteId);
    }

    /**
     * Buscar luminárias por tipo
     */
    @Transactional(readOnly = true)
    public List<Luminaria> findByType(String type) {
        return luminariaRepository.findByTypeIgnoreCase(type);
    }

    /**
     * Buscar luminárias ligadas
     */
    @Transactional(readOnly = true)
    public List<Luminaria> findActiveLuminarias() {
        return luminariaRepository.findByStatusTrue();
    }

    /**
     * Buscar luminárias ligadas por ambiente
     */
    @Transactional(readOnly = true)
    public List<Luminaria> findActiveLuminariasByAmbiente(Long ambienteId) {
        ambienteService.getById(ambienteId);
        return luminariaRepository.findByAmbienteIdAndStatusTrue(ambienteId);
    }

    /**
     * Atualizar luminária
     */
    public Luminaria updateLuminaria(Long id, Luminaria luminariaAtualizada) {
        Luminaria luminariaExistente = getById(id);

        // Verificar se o novo nome já existe no ambiente (excluindo a própria luminária)
        if (!luminariaExistente.getName().equalsIgnoreCase(luminariaAtualizada.getName()) &&
            luminariaRepository.existsByNameIgnoreCaseAndAmbienteIdAndIdNot(
                luminariaAtualizada.getName(), 
                luminariaExistente.getAmbiente().getId(), 
                id)) {
            throw new RuntimeException("Já existe uma luminária com o nome '" + luminariaAtualizada.getName() + 
                                     "' no ambiente '" + luminariaExistente.getAmbiente().getName() + "'");
        }

        // Atualizar campos
        luminariaExistente.setName(luminariaAtualizada.getName());
        luminariaExistente.setType(luminariaAtualizada.getType());
        luminariaExistente.setStatus(luminariaAtualizada.getStatus());
        luminariaExistente.setBrightness(luminariaAtualizada.getBrightness());
        luminariaExistente.setColor(luminariaAtualizada.getColor());
        luminariaExistente.setPositionX(luminariaAtualizada.getPositionX());
        luminariaExistente.setPositionY(luminariaAtualizada.getPositionY());

        return luminariaRepository.save(luminariaExistente);
    }

    /**
     * Deletar luminária
     */
    public void deleteLuminaria(Long id) {
        Luminaria luminaria = getById(id);
        luminariaRepository.delete(luminaria);
    }

    /**
     * Ligar luminária
     */
    public Luminaria turnOnLuminaria(Long id) {
        Luminaria luminaria = getById(id);
        luminaria.turnOn();
        return luminariaRepository.save(luminaria);
    }

    /**
     * Desligar luminária
     */
    public Luminaria turnOffLuminaria(Long id) {
        Luminaria luminaria = getById(id);
        luminaria.turnOff();
        return luminariaRepository.save(luminaria);
    }

    /**
     * Alterar brilho da luminária
     */
    public Luminaria changeBrightness(Long id, Integer brightness) {
        if (brightness < 0 || brightness > 100) {
            throw new RuntimeException("Brilho deve estar entre 0 e 100");
        }
        
        Luminaria luminaria = getById(id);
        luminaria.setBrightness(brightness);
        return luminariaRepository.save(luminaria);
    }

    /**
     * Alterar cor da luminária
     */
    public Luminaria changeColor(Long id, String color) {
        Luminaria luminaria = getById(id);
        luminaria.setColor(color);
        return luminariaRepository.save(luminaria);
    }

    /**
     * Contar luminárias por ambiente
     */
    @Transactional(readOnly = true)
    public long countByAmbiente(Long ambienteId) {
        return luminariaRepository.countByAmbienteId(ambienteId);
    }

    /**
     * Contar luminárias ligadas por ambiente
     */
    @Transactional(readOnly = true)
    public long countActiveLuminariasByAmbiente(Long ambienteId) {
        return luminariaRepository.countByAmbienteIdAndStatusTrue(ambienteId);
    }

    /**
     * Buscar luminárias por termo de pesquisa
     */
    @Transactional(readOnly = true)
    public List<Luminaria> searchLuminarias(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        return luminariaRepository.findByNameContainingIgnoreCase(searchTerm.trim());
    }

    /**
     * Verificar se luminária existe
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return luminariaRepository.existsById(id);
    }
}
