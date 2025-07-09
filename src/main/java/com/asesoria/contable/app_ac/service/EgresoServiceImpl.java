package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.exceptions.ClienteNotFoundException;
import com.asesoria.contable.app_ac.exceptions.EgresoNotFoundException;
import com.asesoria.contable.app_ac.mapper.EgresoMapper;
import com.asesoria.contable.app_ac.model.dto.EgresoRequest;
import com.asesoria.contable.app_ac.model.dto.EgresoResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Egreso;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.EgresoRepository;
import com.asesoria.contable.app_ac.utils.enums.Regimen;
import com.asesoria.contable.app_ac.utils.enums.TipoContabilidad;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EgresoServiceImpl implements EgresoService {

    private final EgresoRepository egresoRepository;
    private final ClienteRepository clienteRepository;
    private final EgresoMapper egresoMapper;

    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");

    @Override
    public EgresoResponse findById(Long id) {
        return egresoRepository.findById(id)
                .map(egresoMapper::toEgresoResponse)
                .orElseThrow(EgresoNotFoundException::new);
    }

    @Override
    public List<EgresoResponse> findAll() {
        return egresoRepository.findAll()
                .stream()
                .map(egresoMapper::toEgresoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EgresoResponse save(EgresoRequest request) {
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        Egreso egreso = egresoMapper.toEgreso(request);
        egreso.setCliente(cliente);

        // Lógica para calcular IGV
        if (request.getTipoTributario() == TipoTributario.GRAVADA &&
                cliente.getRegimen() != Regimen.NRUS) {

            egreso.setMontoIgv(request.getMonto().multiply(IGV_RATE));
        } else {
            egreso.setMontoIgv(BigDecimal.ZERO);
        }

        Egreso egresoGuardado = egresoRepository.save(egreso);
        return egresoMapper.toEgresoResponse(egresoGuardado);
    }

    @Override
    public EgresoResponse update(Long id, EgresoRequest request) {
        Egreso egreso = egresoRepository.findById(id)
                .orElseThrow(EgresoNotFoundException::new);

        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(ClienteNotFoundException::new);

        egresoMapper.updateEgresoFromRequest(request, egreso);
        egreso.setCliente(cliente);

        // Lógica para calcular IGV
        if (request.getTipoTributario() == TipoTributario.GRAVADA &&
                cliente.getRegimen() != Regimen.NRUS) {

            egreso.setMontoIgv(request.getMonto().multiply(IGV_RATE));
        } else {
            egreso.setMontoIgv(BigDecimal.ZERO);
        }

        Egreso egresoActualizado = egresoRepository.save(egreso);
        return egresoMapper.toEgresoResponse(egresoActualizado);
    }

    @Override
    public void deleteById(Long id) {
        if (egresoRepository.findById(id).isEmpty()) {
            throw new EgresoNotFoundException();
        }
        egresoRepository.deleteById(id);
    }

    @Override
    public List<EgresoResponse> findByClienteId(Long usuarioId) {
        return egresoRepository.findByClienteId(usuarioId)
                .stream()
                .map(egresoMapper::toEgresoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EgresoResponse saveByUsuario(EgresoRequest request, Usuario usuario) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(ClienteNotFoundException::new);

        Egreso egreso = egresoMapper.toEgreso(request);
        egreso.setCliente(cliente);

        // Lógica para calcular IGV
        if (request.getTipoTributario() == TipoTributario.GRAVADA &&
                cliente.getRegimen() != Regimen.NRUS) {

            egreso.setMontoIgv(request.getMonto().multiply(IGV_RATE));
        } else {
            egreso.setMontoIgv(BigDecimal.ZERO);
        }

        return egresoMapper.toEgresoResponse(egresoRepository.save(egreso));
    }

    @Override
    public EgresoResponse updateMyEgreso(Long id, EgresoRequest request, Usuario usuario) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(ClienteNotFoundException::new);

        Egreso egreso = egresoRepository.findById(id)
                .orElseThrow(EgresoNotFoundException::new);

        if (!egreso.getCliente().getId().equals(cliente.getId())) {
            throw new AccessDeniedException("No puedes modificar este egreso.");
        }

        egresoMapper.updateEgresoFromRequest(request, egreso);
        egreso.setCliente(cliente);

        // Lógica para calcular IGV
        if (request.getTipoTributario() == TipoTributario.GRAVADA &&
                cliente.getRegimen() != Regimen.NRUS) {

            egreso.setMontoIgv(request.getMonto().multiply(IGV_RATE));
        } else {
            egreso.setMontoIgv(BigDecimal.ZERO);
        }

        return egresoMapper.toEgresoResponse(egresoRepository.save(egreso));
    }

    @Override
    public void deleteMyEgreso(Long id, Usuario usuario) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(ClienteNotFoundException::new);

        Egreso egreso = egresoRepository.findById(id)
                .orElseThrow(EgresoNotFoundException::new);

        if (!egreso.getCliente().getId().equals(cliente.getId())) {
            throw new AccessDeniedException("No puedes eliminar este egreso.");
        }

        egresoRepository.deleteById(id);
    }

    @Override
    public List<EgresoResponse> findByUsuarioId(Long usuarioId) {
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(ClienteNotFoundException::new);

        return egresoRepository.findByClienteId(cliente.getId())
                .stream()
                .map(egresoMapper::toEgresoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calcularTotalMesActual(Long clienteId) {
        YearMonth mesActual = YearMonth.now();
        LocalDate inicioMes = mesActual.atDay(1);
        LocalDate finMes = mesActual.atEndOfMonth();
        return egresoRepository.sumMontoByClienteIdAndFechaBetween(clienteId, inicioMes, finMes);
    }

    @Override
    public BigDecimal calcularTotalMesAnterior(Long clienteId) {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicioMes = mesAnterior.atDay(1);
        LocalDate finMes = mesAnterior.atEndOfMonth();
        return egresoRepository.sumMontoByClienteIdAndFechaBetween(clienteId, inicioMes, finMes);
    }

    @Override
    public Map<String, BigDecimal> obtenerEgresosPorTipoContabilidad(Long clienteId) {
        List<Object[]> resultados = egresoRepository.sumRawByTipoContabilidad(clienteId);

        return resultados.stream()
                .collect(Collectors.toMap(
                        row -> ((TipoContabilidad) row[0]).name(),   // convertimos enum a String
                        row -> (BigDecimal) row[1]
                ));
    }

    @Override
    public Map<String, BigDecimal> obtenerEgresosPorTipoTributario(Long clienteId) {
        List<Object[]> resultados = egresoRepository.sumRawByTipoTributario(clienteId);
        Map<String, BigDecimal> resultadoMap = new HashMap<>();

        for (Object[] fila : resultados) {
            TipoTributario tipo = (TipoTributario) fila[0];
            BigDecimal total = (BigDecimal) fila[1];
            resultadoMap.put(tipo.name(), total);
        }

        return resultadoMap;
    }

    @Override
    public List<Map<String, Object>> identificarEgresosRecurrentes(Long clienteId) {
        return egresoRepository.findEgresosRecurrentes(clienteId);
    }

    @Override
    public BigDecimal getSumaEgresosGravadosMesAnterior(Long clienteId) {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicioMes = mesAnterior.atDay(1);
        LocalDate finMes = mesAnterior.atEndOfMonth();
        return egresoRepository.sumMontoByClienteIdAndTipoTributarioAndFechaBetween(clienteId, TipoTributario.GRAVADA, inicioMes, finMes);
    }

    @Override
    public BigDecimal getSumaEgresosExoneradosMesAnterior(Long clienteId) {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicioMes = mesAnterior.atDay(1);
        LocalDate finMes = mesAnterior.atEndOfMonth();
        return egresoRepository.sumMontoByClienteIdAndTipoTributarioAndFechaBetween(clienteId, TipoTributario.EXONERADA, inicioMes, finMes);
    }

    @Override
    public BigDecimal getSumaEgresosInafectosMesAnterior(Long clienteId) {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicioMes = mesAnterior.atDay(1);
        LocalDate finMes = mesAnterior.atEndOfMonth();
        return egresoRepository.sumMontoByClienteIdAndTipoTributarioAndFechaBetween(clienteId, TipoTributario.INAFECTA, inicioMes, finMes);
    }

    @Override
    public BigDecimal getSumaIgvEgresosMesAnterior(Long clienteId) {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicioMes = mesAnterior.atDay(1);
        LocalDate finMes = mesAnterior.atEndOfMonth();
        return egresoRepository.sumMontoIgvByClienteIdAndFechaBetween(clienteId, inicioMes, finMes);
    }
}
