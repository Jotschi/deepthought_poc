# pip install pyarrow
# pip install pandas

import pyarrow.parquet as pq

def convert(name):
    # columns=['col1', 'col2'] to restrict loaded columns
    pds = pq.read_pandas(name, columns=None).to_pandas()

    # path_or_buf='output.jsonl.gz' to output to a file instead of stdout
    pds.to_json(path_or_buf=name + ".jsonl", orient='records', lines=True, date_format='iso', date_unit='us', compression=None)
    
    
convert("test-00000-of-00001-a0c917350be4ccd9.parquet")
convert("validation-00000-of-00001-a11e438be202af33.parquet")