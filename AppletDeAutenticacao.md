# Applet para Autenticação #
Está disponível no [repositório](http://code.google.com/p/poreid/source/browse/#svn%2Ftrunk%2Fapplet.autenticacao) um exemplo completo de uma applet para autenticação de utilizadores com recurso ao cartão de cidadão através do componente POReID.

A applet funciona apenas com https. Será necessário assinar a applet com um certificado para assinar código.

Para além da applet, será necessário um ficheiro jnlp. No exemplo fornecido mais abaixo o ficheiro estaria localizado em https://www.poreid.org/jnlp/applet-autenticacao.jnlp e o jar comprimido estaria em https://www.poreid.org/jnlp/codebase/applet.autenticacao-1.0.jar.pack.gz
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE jnlp PUBLIC "-//Sun Microsystems, Inc//DTD JNLP Descriptor 6.0//EN" "http://java.sun.com/dtd/JNLP-6.0.dtd">
<jnlp spec="6.0+" codebase="https://www.poreid.org/jnlp/" href="applet-autenticacao.jnlp">
    <information>
        <title>Applet de Autenticação</title>
        <vendor>www.poreid.org</vendor>
        <homepage href="www.poreid.org"/>
    </information>
    <security>
        <all-permissions/>
    </security>
    <update check="always" policy="always" />
    <resources>
        <property name="jnlp.packEnabled" value="true"/>
        <j2se version="1.7+"/>
        <jar href="codebase/applet.autenticacao-1.0.jar.pack.gz" main="true"/>
    </resources>
  
    <applet-desc
        name="Applet de Autenticação"
        main-class="org.poreid.applet.autenticacao.AppletAutenticacao"
        width="1"
        height="1">
        <param name="separate_jvm" value="true" />
    </applet-desc>
</jnlp>
```

Tipicamente a applet é invocada da seguinte forma numa página html:

```
<script type="text/javascript">
	var attributes = {
		id: 'AppletAutenticacao',
		width: 1,
		height: 1
	}

	var parameters = {
		'jnlp_href': 'jnlp/applet-autenticacao.jnlp',
		'nonce': '2f9d3f35-807c-409c-a443-deb52c831d17',
		'post.url': 'https://www.poreid.org',
		'data.requested': 'morada',		
	}

	deployJava.runApplet(attributes, parameters, "1.7.0_55"); 
</script>
```

As propriedades recebidas pela applet são:
  * **nonce** - poderá ser um guid/uuid (**propriedade obrigatória**)
  * **post.url** - url (apenas https) para onde ser efetuado um post em caso de sucesso/erro (**propriedade obrigatória**)
  * **data.requested** - dados do cidadão a requisitar, valores possiveis: _**id**_, _**morada**_ e _**foto**_, combinações possiveis: qualquer combinação ou nenhuma. Em formato csv.


A applet efetuará um post em https para o url indicado na propriedade post.url, as variáveis POST que o servidor receberá poderão ser:

Em caso de sucesso:
  * opcionais:
    * id
    * morada
    * foto
    * sod

  * obrigatórias:
    * certificado
    * assinatura
    * nonce

Em caso de erro:
  * obrigatórias:
    * mensagem
    * excecao


Validações necessárias no servidor:
  * validar estado do certificado do cidadão
  * validar assinatura

Consultar página RecolhaVerificacaoDosDados para mais detalhes relativos à assinatura.

As variaveis opcionais (id, morada, foto, sod) carecem de validação no lado do servidor! Consultar página RecolhaVerificacaoDosDados para mais detalhes.