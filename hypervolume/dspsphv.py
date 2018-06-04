import os
import csv
from glob import glob

from hypervolume import hypervolume as hv


BASE_DIR = "results"


def list_result_files(result_type="NOB", base_dir=BASE_DIR):
    path_pattern = "../{}/**/{}/*.csv".format(BASE_DIR, result_type)
    return glob(path_pattern, recursive=True)


def dim(fname):
    with open(fname, "r") as f:
        return len(f.readline().split(" "))


def extract_instance_data(fname):
    filepath, filename = os.path.split(fname)
    filename, ext = os.path.splitext(filename)
    tmp = filename.split("-")
    return [tmp[1], tmp[2] + "-" + tmp[3], tmp[4], tmp[5]]


def get_reference_string(fname):
    return " ".join(["1.1" for _ in range(dim(fname))])


def main():
    with open("metrics.csv", "w") as out:
        writer = csv.writer(out, delimiter=" ")
        for fname in list_result_files():
            data = extract_instance_data(fname)
            data.append(hv(fname, get_reference_string(fname)))
            writer.writerow(data)


if __name__ == '__main__':
    main()
