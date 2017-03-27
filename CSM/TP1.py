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

#
# def SNR(img_original, img_transformada):
#     # colocar um for
#     Pplano0 = sum(img1[::0] ** 2) / len(img1[::0])
#     Pplano1 = sum(img1[::1] ** 2) / len(img1[::1])
#     Pplano2 = sum(img1[::2] ** 2) / len(img1[::2])
#     nPixeis =
#     Pimagem = (Pplano0 + Pplano1 + Pplano2) / nPixeis
#
#     erro = x_img - img1
#     # colocar um for
#     PEplano0 = sum(erro[::0] ** 2) / len(erro[::0])
#     PEplano1 = sum(erro[::1] ** 2) / len(erro[::1])
#     PEplano2 = sum(erro[::2] ** 2) / len(erro[::2])
#     nPixeis =
#     Perro = (PEplano0 + PEplano1 + PEplano2) / nPixeis
#     SNR = 10 * m.log10(Pimagem / Perro)  # SNR pratica
#
#     return SNR

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

"""
Os pixeis da imagem estão distribuidos entre os niveis de cinzento 30 e 230, sendo que os picos de distribuiçºao estão a
volta dos niveis 50, 100 e 150. A imagem tem 256 niveis diferentes de cinzento.
"""

# 5

"""
Nos próximos trabalhos será necessário realizar operações com os valores de cada pixel. Para este efeito pode-
se transformar a imagem para um array. O código seguinte representa o pixel mais signiﬁcante da imagem.
Apresente oito imagens, cada uma com o valor de cada bit para todos os pixeis.
"""

def bitvalue(img_g):
    #array com o valor de cada bit
    bits = [1,2,4,8,16,32,64,128]
    # numero de colunas e linhas do array bidimensional
    cols = len(img_g)
    rows = len(img_g[0])
    #itera cada bit
    for i in range(len(bits)):
        #em cada bit cria uma imagem para representar o plano de bits
        y = np.zeros((cols, rows), dtype=np.uint8)
        #itera nas linhas e colunas
        for z in range(len(img_g)):
            for t in range(len(img_g[z])):
                #avalia a expressão binaria em 8 bits para saber se o bit esta activo
                if '{0:08b}'.format(img_g[z][t])[7-i] == '1':
                    # se o bit esta activo o pixel é guardado com o valor respectivo
                    y[z][t] = bits[i]
        cv2.imshow('Bit Plane - bit %i' % i, y * 1.0)
        cv2.imwrite('Bit Plane - bit %i.bmp' % i, img_g)


bitvalue(x_img_g)

# 6

"""
Grave uma imagem que contém apenas a informação dos 4 bits mais signiﬁcantes da imagem.
"""

def mostSigBits(img_g):
    #array com o valor de cada bit
    bits = [1,2,4,8,16,32,64,128]
    # numero de colunas e linhas do array bidimensional
    cols = len(img_g)
    rows = len(img_g[0])
    # array de destino iniciado a zeros
    y = np.zeros((cols, rows), dtype=np.uint8)
    #itera os 4 bits mais significantes
    for i in range(len(bits) / 2, len(bits)):
        #itera a imagem nas linhas e colunas
        for z in range(len(img_g)):
            for t in range(len(img_g[z])):
                #avalia a expressão binaria em 8 bits para saber se o bit esta activo
                if '{0:08b}'.format(img_g[z][t])[7-i] == '1':
                    # se o bit esta activo o valor do bit é adicionado ao pixel
                    y[z][t] += bits[i]
    cv2.imshow('Imagem com os 4 bits mais significativos', y)
    cv2.imwrite('4bitsig.bmp', img_g)

mostSigBits(x_img_g)

# 7

"""
Crie uma função que apresente uma imagem (100 × 100) como se apresenta na ﬁgura. O ângulo de cada sector
é dado por parâmetro passado à função (o ângulo é um valor inteiro entre 0 e 360 graus).
"""

#7 -> criar um array a branco arr=np.ones((4,4))*255
#  -> calcular o angulo com alpha=np.arctan(y/x.)*180/np.pi
#  ->
#  ->

def create_blank(width, height, rgb_fundo=(0, 0, 0), rgb_cor=(0, 0, 0)):
    # Create black blank image
    image = np.ones((height, width, 3), np.uint8)
    # Since OpenCV uses BGR, convert the color first
    color = tuple(reversed(rgb_fundo))
    # Fill image with color
    image[:] = color
    #alpha = np.arctan(250 / 5) * 180 / np.pi
    Thickness = 1

    y = 0
    while y <= 500:
        cv2.line(image, (width/2, height/2), (width, y), rgb_cor, Thickness)
        cv2.line(image, (width/2, height/2), (0, y), rgb_cor, Thickness)
        y += 10
    x = 0
    while x <= 500:
        cv2.line(image, (width/2, height/2), (x, 0), rgb_cor, Thickness)
        cv2.line(image, (width/2, height/2), (x, height), rgb_cor, Thickness)
        x += 10

    return image

width, height = 500, 500
branco = (255, 255, 255)
preto = (0, 0, 0)


image = create_blank(width, height, branco, preto)
cv2.imshow('Invencao', image)
cv2.waitKey(0)
cv2.destroyAllWindows()
#cv2.imwrite('test.jpg', image)