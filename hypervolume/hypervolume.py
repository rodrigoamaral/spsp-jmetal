import subprocess


def hypervolume(filepath, reference_string, encoding="utf-8"):
    cmd = './hv -r "{}" {}'.format(reference_string, filepath)
    output = subprocess.check_output(cmd, shell=True)
    hv = float(output.decode(encoding).strip())
    return hv
