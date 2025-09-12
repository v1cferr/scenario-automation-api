package com.scenario.automation.service;

import com.scenario.automation.client.ImagesApiClient;
import com.scenario.automation.model.Ambiente;
import com.scenario.automation.repository.AmbienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AmbienteService {

    @Autowired
    private AmbienteRepository ambienteRepository;

    @Autowired
    private ImagesApiClient imagesApiClient;

    /**
     * Criar novo ambiente
     */
    public Ambiente createAmbiente(Ambiente ambiente) {
        // Verificar se já existe ambiente com o mesmo nome
        if (ambienteRepository.existsByNameIgnoreCase(ambiente.getName())) {
            throw new RuntimeException("Já existe um ambiente com o nome: " + ambiente.getName());
        }
        return ambienteRepository.save(ambiente);
    }

    /**
     * Buscar ambiente por ID
     */
    @Transactional(readOnly = true)
    public Optional<Ambiente> findById(Long id) {
        return ambienteRepository.findById(id);
    }

    /**
     * Buscar ambiente por ID (com exceção se não encontrar)
     */
    @Transactional(readOnly = true)
    public Ambiente getById(Long id) {
        return ambienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ambiente não encontrado com ID: " + id));
    }

    /**
     * Listar todos os ambientes
     */
    @Transactional(readOnly = true)
    public List<Ambiente> findAll() {
        return ambienteRepository.findAllByOrderByNameAsc();
    }

    /**
     * Listar ambientes com paginação
     */
    @Transactional(readOnly = true)
    public Page<Ambiente> findAll(Pageable pageable) {
        return ambienteRepository.findAll(pageable);
    }

    /**
     * Buscar ambientes por termo de pesquisa
     */
    @Transactional(readOnly = true)
    public List<Ambiente> searchAmbientes(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        return ambienteRepository.findByNameOrDescriptionContainingIgnoreCase(searchTerm.trim());
    }

    /**
     * Atualizar ambiente
     */
    public Ambiente updateAmbiente(Long id, Ambiente ambienteAtualizado) {
        Ambiente ambienteExistente = getById(id);

        // Verificar se o novo nome já existe (excluindo o próprio ambiente)
        if (!ambienteExistente.getName().equalsIgnoreCase(ambienteAtualizado.getName()) &&
            ambienteRepository.existsByNameIgnoreCaseAndIdNot(ambienteAtualizado.getName(), id)) {
            throw new RuntimeException("Já existe um ambiente com o nome: " + ambienteAtualizado.getName());
        }

        // Atualizar campos
        ambienteExistente.setName(ambienteAtualizado.getName());
        ambienteExistente.setDescription(ambienteAtualizado.getDescription());

        return ambienteRepository.save(ambienteExistente);
    }

    /**
     * Deletar ambiente
     */
    public void deleteAmbiente(Long id) {
        Ambiente ambiente = getById(id);
        
        // Primeiro, deletar todas as imagens associadas ao ambiente
        try {
            int deletedImagesCount = imagesApiClient.deleteAllImagesByEnvironment(id);
            System.out.println("Deletadas " + deletedImagesCount + " imagens do ambiente " + id);
        } catch (Exception e) {
            // Log do erro mas continua com a exclusão do ambiente
            System.err.println("Erro ao deletar imagens do ambiente " + id + ": " + e.getMessage());
        }
        
        // Depois, deletar o ambiente
        ambienteRepository.delete(ambiente);
    }

    /**
     * Verificar se ambiente existe
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return ambienteRepository.existsById(id);
    }

    /**
     * Verificar se nome já existe
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return ambienteRepository.existsByNameIgnoreCase(name);
    }

    /**
     * Buscar ambientes com suas luminárias
     */
    @Transactional(readOnly = true)
    public List<Ambiente> findAllWithLuminarias() {
        return ambienteRepository.findAllWithLuminarias();
    }

    /**
     * Buscar ambientes por subambiente
     */
    @Transactional(readOnly = true)
    public List<Ambiente> findBySubambiente(String subambiente) {
        return ambienteRepository.findBySubambienteIgnoreCase(subambiente);
    }

    /**
     * Buscar todos os subambientes únicos
     */
    @Transactional(readOnly = true)
    public List<String> findAllDistinctSubambientes() {
        return ambienteRepository.findAllDistinctSubambientes();
    }

    /**
     * Verificar se existe ambiente com o subambiente
     */
    @Transactional(readOnly = true)
    public boolean existsBySubambiente(String subambiente) {
        return ambienteRepository.existsBySubambienteIgnoreCase(subambiente);
    }
}
