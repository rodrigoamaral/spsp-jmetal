import pandas as pd
import matplotlib.pyplot as plt


def algorithm_dataset(raw_data, algorithm_id, sorted_by="event", zero_values=False):
    data = raw_data[raw_data.algorithm == algorithm_id]
    data = data.sort_values(by=sorted_by)
    if not zero_values:
        data = data[data.hypervolume != 0]
    return data


def instance_dataset(raw_data, instance_id, sorted_by="event", zero_values=False):
    data = raw_data[raw_data.instance == instance_id]
    data = data.sort_values(by=sorted_by)
    if not zero_values:
        data = data[data.hypervolume != 0]
    return data


def comparison_dataset(ds):
    return ds.groupby(["algorithm", "event"], as_index=False)["hypervolume"].mean()


def mean_hypervolume(ds):
    return ds.groupby(["instance", "algorithm"], as_index=False)["hypervolume"].agg(['mean', 'std'])


def max_event(ds):
    return ds["event"].max()


def metrics_dataset(filepath="../hypervolume/metrics.csv"):
    column_labels = ["algorithm", "instance", "execution", "event", "hypervolume"]
    data = pd.read_csv(filepath,
                       delimiter=" ",
                       header=None,
                       names=column_labels,
                       # index_col=column_labels[0]
                       )
    return data
