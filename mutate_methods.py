import multiprocessing as mp
import os
import sys


MUTATOR="target/cug-1.0-SNAPSHOT.jar"
NUM_THREADS=1
MUTATION_LEVEL = sys.argv[2]
REMOVE="rm"
# REMOVE="del"

def mutate_file(path:str):

    os.system("java -cp " + MUTATOR + " Main --mutate_methods --file " + path + " --mutation_level " + MUTATION_LEVEL + " --num_threads "  + str(NUM_THREADS))

if __name__ == "__main__":

    mypath = sys.argv[1]
    alldirs = [os.path.join(mypath, f).replace("\\", "/") for f in os.listdir(mypath) if os.path.isfile(os.path.join(mypath, f))]

    print(alldirs)
    pool = mp.Pool(processes=mp.cpu_count())
    pool.map(mutate_file, alldirs)