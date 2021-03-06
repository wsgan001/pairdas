package com.upenn.parsers;

import htsjdk.samtools.util.Interval;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by cheng on 2/13/15.
 * take in a gtf file, based on ensemble gtf, and return a map of gene_name to genes*
 */
public class GTFParser {
    Map<String, GeneInfo> geneid_to_geneinfo = new TreeMap<String, GeneInfo>();
    boolean genes_processed = false;
    public GTFParser(File gtf_fn){
        try {
            BufferedReader gtf_fh;
            if (FilenameUtils.getExtension(gtf_fn.getName()).equals("gz")){
                GZIPInputStream gtf_gzip = new GZIPInputStream(new FileInputStream(gtf_fn));
                gtf_fh = new BufferedReader(new InputStreamReader(gtf_gzip));
            } else {
                gtf_fh = new BufferedReader(new FileReader(gtf_fn));
            }

            String line = null;
            while ((line = gtf_fh.readLine())!= null) {
                String gene_name = TranscriptInfo.extract_attribute(line.split("\t")[8], "gene_id");

                if (geneid_to_geneinfo.containsKey(gene_name)){
                    geneid_to_geneinfo.get(gene_name).add_line(line);
                } else {
                    geneid_to_geneinfo.put(gene_name,new GeneInfo(gene_name));
                    geneid_to_geneinfo.get(gene_name).add_line(line);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public int get_number_genes(){
        return geneid_to_geneinfo.size();
    }
    
    public GeneInfo get_gene (String gene_name){
        return this.geneid_to_geneinfo.get(gene_name);
    }
    
    public Map<String, GeneInfo> get_all_genes(){
        return this.geneid_to_geneinfo;
    }
    
    public void process_genes(){

        if (this.genes_processed==false){
            for(Iterator<Map.Entry<String, GeneInfo>> it = this.geneid_to_geneinfo.entrySet().iterator(); it.hasNext(); ){
                Map.Entry<String, GeneInfo> entry = it.next();
                entry.getValue().get_tx_interval_matrix();
            }
            for(Iterator<Map.Entry<String, GeneInfo>> it = this.geneid_to_geneinfo.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, GeneInfo> entry = it.next();
                if (entry.getValue().getTx_num()<2 || (!entry.getValue().has_strand) || (entry.getValue().if_empty_after_trim())){
                    it.remove();
                }
            }
            

        }
        this.genes_processed=true;
    }
    

    public void print_intervals_saf_format(){
        if (this.genes_processed == false) {
            this.process_genes();
        }
        System.out.println("GeneID\tChr\tStart\tEnd\tStrand");
        for(Iterator<Map.Entry<String, GeneInfo>> it = this.geneid_to_geneinfo.entrySet().iterator(); it.hasNext(); ){
            Map.Entry<String, GeneInfo> entry = it.next();
            entry.getValue().print_saf();
        }
    }

    public void print_intervals_iaf_format() {
        if (this.genes_processed == false) {
            this.process_genes();
        }
        for (Iterator<Map.Entry<String, GeneInfo>> it = this.geneid_to_geneinfo.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, GeneInfo> entry = it.next();
            entry.getValue().print_iaf();
        }
    }

    
}
