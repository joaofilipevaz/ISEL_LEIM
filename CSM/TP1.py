# This Python file uses the following encoding: utf-8

# Trabalho 1 CSM

import cv2
import numpy as np
import matplotlib.pyplot as plt

# 1

x_img = cv2.imread("lenac.tif")
cv2.imshow('Original Image', x_img)
# metodo que retorna o data type da imagem
print x_img.dtype
# metodo que retorna o numero de linhas, colunas e canais de cor. o numero de canais só aparece se a imagem for a cores
print x_img.shape
cv2.waitKey(0)
cv2.destroyAllWindows()

# 2

"""
Grave a mesma imagem, mas agora em formato "JPEG"com diferentes qualidades. Veriﬁque visualmente a
qualidade das imagens assim como o tamanho do ﬁcheiro. Calcule a taxa de compressão, a SNR e a PSNR.
"""

cv2.imwrite('file1.jpg', x_img, (cv2.IMWRITE_JPEG_QUALITY, 80))
cv2.imwrite('file2.jpg', x_img, (cv2.IMWRITE_JPEG_QUALITY, 10))

# 3

"""
Converta a imagem para níveis de cinzento, usando o método "cvtColor"e grave a imagem. Este método aplica
a transformação Y = R∗299/1000+G∗587/1000+B ∗114/1000, justiﬁque a utilização desta equação. Veriﬁque
também o tamanho do ﬁcheiro e compare-o com o ﬁcheiro original.

R -
A equação Y = R∗299/1000+G∗587/1000+B ∗114/1000 é conhecida como a "ITU-R 601-2 luma transform" e é um standard
para a representação da luma, também conhecida pela letra Y. A luma representa o brilho de uma imagem,
tipicamente representado a preto e branco e acromatico. O olho humano tem uma sensibilidade superior a luminancia do que
relativamente às diferenças cromáticas.
O calculo directo da luminancia obriga a analises espectrais particulares,
outra forma de a intrepretar é atraves de uma soma ponderada dos componentes RGB.
Analisando as 3 cores vermelho, verde e azul, e tendo elas a mesma radiancia no espectro visivel, entao o verde
irá aparecer como a mais brilhante das tres já que a função de eficiencia de luminosidade (que nos fornece
uma representação fiedigna da sensibilidade do olho humano à luminosidade) atinge o pico nesta gama do espectro de cores.
O vermelho será menos brilhante e o azul o mais escuro dos tres. Os coeficientes são então uma função de cada componente
espectral devidamente ponderado pela sensibilidade do olho humano. A origem destes coeficientes foi a de servir como referencia
para a computação da luminancia nos monitores CRT introduzidos pela TV em 1953. Embora estes coeficientes sejam ainda adequados para
o calculo da luma já nao reflectem a luminancia nos monitores comtemporaneos.
Na realidade o tamanho do ficheiro é um terço do ficheiro original, este facto é explicado pelo facto da luma, ou Y, ser
apenas um dos canais dos três que compoem o ficheiro, praticamente o que estamos a fazer é a suprimir a componente chromatica e
a ficar apenas com a compoente da intensidade da luz.

"""

x_img_g = cv2.cvtColor(x_img, cv2.COLOR_BGR2GRAY)
cv2.imshow('Gray Image', x_img_g)
cv2.waitKey(0)
cv2.destroyAllWindows()
cv2.imwrite('file3.bmp', x_img_g)



# 4

"""
Apresente o histograma da imagem em tons de cizento, veriﬁque quantos níveis de cizento tem a imagem.
"""

plt.hist(x_img_g.ravel(), 256, [0, 256])

# 5

"""
Nos próximos trabalhos será necessário realizar operações com os valores de cada pixel. Para este efeito pode-
se transformar a imagem para um array. O código seguinte representa o pixel mais signiﬁcante da imagem.
Apresente oito imagens, cada uma com o valor de cada bit para todos os pixeis.
"""

y = x_img_g > 128
cv2.imshow('BW', y*1.0)

# 6

"""
Grave uma imagem que contém apenas a informação dos 4 bits mais signiﬁcantes da imagem.
"""

# y= . . .
cv2.imwrite('lena_4 .bmp', y)

# 7

"""
Crie uma função que apresente uma imagem (100 × 100) como se apresenta na ﬁgura. O ângulo de cada sector
é dado por parâmetro passado à função (o ângulo é um valor inteiro entre 0 e 360 graus).
"""