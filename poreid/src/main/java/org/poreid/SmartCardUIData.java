/*
 * The MIT License
 *
 * Copyright 2014, 2015, 2016 Rui Martinho (rmartinho@gmail.com), António Braz (antoniocbraz@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.poreid;

/**
 * Apresentar conteúdo no diálogo de escolha multipla
 * @author POReID
 */
public interface SmartCardUIData {

    /**
     * Retorna o icone associado ao tipo de cartão
     * @return icone 
     */
    byte[] getIcon();

    /**
     * Retorna a informação a apresentar ao utilizador de forma a possibilitar a seleção de um cartão caso exista mais do um no sistema.
     * @return informação de apresentação (por exemplo nome completo)
     */
    String getUIVisibleInfo();

    /**
     * Devolve uma tooltip
     * @return tooltip
     */
    String getTooltip();

    /**
     * Devolve uma descrição dos dados (Acessibilidade)
     * @return descrição dos dados
     */
    String getDescription();
}
