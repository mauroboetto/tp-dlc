#!/usr/bin/env python3

import random
import os

dir = 'web/test-documents'
words = ['perro', 'gato', 'raton', 'canario', 'serpiente', 'elefante']

base_num = len(os.listdir(dir)) + 1

for i in range(30):
    words_to_add = []
    for j in range(random.randint(0, 7)):
        word_to_add = random.choice(words)
        words_to_add.extend([word_to_add] * random.randint(1, 20))

    with open(os.path.join(dir, 'doc{}.txt'.format(i + base_num)), 'w') as f:
        f.write('\n'.join(words_to_add))
        
            
            


