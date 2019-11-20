package com.infoway.banking.utils;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Cliente;
import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Transacao;
import com.infoway.banking.enums.TipoTransacao;

public class TesteUtils {
	
	public static final int BANCO_001 = 0;
	public static final int BANCO_260 = 1;
	public static final int CLIENTE_70336818017 = 10;
	public static final int CLIENTE_20867531010 = 11;
	public static final int CONTA_1234567 = 20;
	public static final int CONTA_0000001 = 21;
	public static final int TRANSFERENCIA_100 = 30;
	public static final int DEPOSITO_200 = 31;
	public static final int SAQUE_50 = 32;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static Banco criarBanco(Integer banco) {
		Banco b = new Banco();
		b.setAccountNonExpired(true);
		b.setAccountNonLocked(true);
		b.setCredentialsNonExpired(true);
		b.setEnabled(true);
		switch (banco == null ? -1 : banco) {
		case BANCO_001:
			b.setCodigo("001");
			b.setNome("Banco do Brasil S.A.");
			b.setEmail("ouvidoria@bb.com.br");
			b.setPassword("{bcrypt}" + SenhaUtils.criptografar("fila"));
			b.setUsername("bb");
			break;
			
		case BANCO_260:
			b.setCodigo("260");
			b.setNome("Nu Pagamentos S.A.");
			b.setEmail("meajuda@nubank.com.br");
			b.setPassword("{bcrypt}" + SenhaUtils.criptografar("agencia"));
			b.setUsername("nu");
			break;

		default:
			b = null;
			break;
		}
		return b;
	}
	
	public static Cliente criarCliente(Integer cliente) {
		Cliente c = new Cliente();
		c.setAccountNonExpired(true);
		c.setAccountNonLocked(true);
		c.setCredentialsNonExpired(true);
		c.setEnabled(true);
		switch (cliente == null ? -1 : cliente) {
		case CLIENTE_70336818017:
			c.setCpf("70336818017");
			c.setNome("Jaiminho");
			c.setEmail("jaiminho@carteiro.com");
			c.setUsername("jaiminho");
			c.setPassword("{bcrypt}" + SenhaUtils.criptografar("tangamandapio"));
			break;
			
		case CLIENTE_20867531010:
			c.setCpf("20867531010");
			c.setNome("Clotilde");
			c.setEmail("clotilde@bruxa.com");
			c.setUsername("clotilde");
			c.setPassword("{bcrypt}" + SenhaUtils.criptografar("madruguinha"));
			break;

		default:
			c = null;
			break;
		}
		return c;
	}
	
	public static Conta criarConta(Integer conta, Integer banco, Integer cliente) {
		Conta c = new Conta();
		c.setBanco(criarBanco(banco));
		c.setCliente(criarCliente(cliente));
		switch (conta == null ? -1 : conta) {
		case CONTA_1234567:
			c.setNumero("1234567");
			break;
			
		case CONTA_0000001:
			c.setNumero("0000001");
			break;

		default:
			c = null;
			break;
		}
		return c;
	}
	
	public static Transacao criarTransacao(Integer transacao,
			Integer bancoOrigem, Integer contaOrigem, Integer clienteOrigem,
			Integer bancoDestino, Integer contaDestino, Integer clienteDestino) {
		Transacao t = new Transacao();
		t.setOrigem(criarConta(contaOrigem, bancoOrigem, clienteOrigem));
		t.setDestino(criarConta(contaDestino, bancoDestino, clienteDestino));
		switch (transacao == null ? -1 : transacao) {
		case TRANSFERENCIA_100:
			t.setTipo(TipoTransacao.TRANSFERENCIA);
			t.setValor(100.0);
			break;
			
		case DEPOSITO_200:
			t.setTipo(TipoTransacao.DEPOSITO);
			t.setValor(200.0);
			break;
			
		case SAQUE_50:
			t.setTipo(TipoTransacao.SAQUE);
			t.setValor(50.0);
			break;

		default:
			t = null;
			break;
		}
		return t;
	}
	
	public static void testarRequisicaoInvalida(Object objectDto, String codigoErro, String url, MockMvc mvc, MessageSource ms) throws Exception {
		mvc.perform(MockMvcRequestBuilders.post(url)
				.content(mapper.writeValueAsString(objectDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").isEmpty())
				.andExpect(jsonPath("$.errors").value(ms.getMessage(codigoErro, null, Locale.US)));
	}
	
	public static void testarRequisicaoInvalidaAutenticada(
			Object objectDto, String codigoErro, String url, String token, MockMvc mvc, MessageSource ms) throws Exception {
		mvc.perform(MockMvcRequestBuilders.post(url)
				.header("Authorization", "Bearer " + token)
				.content(mapper.writeValueAsString(objectDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").isEmpty())
				.andExpect(jsonPath("$.errors").value(ms.getMessage(codigoErro, null, Locale.US)));
	}
	
	public static String obterToken(String nomeUsuario, String senha, MockMvc mvc) throws Exception {
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("grant_type", "password");
	    params.add("client_id", "cliente");
	    params.add("username", nomeUsuario);
	    params.add("password", senha);
	    ResultActions result =
	    		mvc.perform(post("/oauth/token")
	    			.params(params)
	    			.with(httpBasic("cliente","senha"))
	    			.accept("application/json;charset=UTF-8"))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType("application/json;charset=UTF-8"));
	    String resultString = result.andReturn().getResponse().getContentAsString();
	    JacksonJsonParser jsonParser = new JacksonJsonParser();
	    return jsonParser.parseMap(resultString).get("access_token").toString();
	}
	
}
