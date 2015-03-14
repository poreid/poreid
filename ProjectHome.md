Sendo um componente desenvolvido em Java recorrendo à API Java Smart Card I/O, permite a manipulação das operações disponíveis no smart card que constitui o documento de identificação eletrónica do cidadão português.
<br />
Facilita a execução de instruções de leitura de ficheiros ou parte específicas destes para a obtenção de dados públicos de identificação do Cartão de Cidadão, assim como os dados de morada protegidos por PIN.
<br />
Constitui-se um JCA Security Provider facilitando a execução de operações criptográficas de assinatura digital usando o certificado de assinatura digital qualificada ou de autenticação disponíveis no smart card.

Temos consciência de que existe muito pouca informação no que diz respeito a operações criptográficas e sobre ficheiros do Cartão de Cidadão. Embora não sejamos autoridade na matéria, estamos disponíveis a partilhar o nosso conhecimento arduamente adquirido com quem se depare com as dificuldades com que já nos debatemos e tenha necessidade de criar software que use este mecanismo extraordinário que é o Cartão de Cidadão. Por isso, se for o caso, não hesite em nos contactar.
<br />
![http://poreid.googlecode.com/svn/wiki/images/email.png](http://poreid.googlecode.com/svn/wiki/images/email.png)

---

Num projeto com maven basta adicionar
```
<dependency>
  <groupId>org.poreid</groupId>
  <artifactId>poreid</artifactId>
  <version>1.47</version>
</dependency>
```

[![](https://scan.coverity.com/projects/3946/badge.svg)](https://scan.coverity.com/projects/3946)