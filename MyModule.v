module MyModule (
    input             clk,
    input             rst_n,
    input      [31:0] a,
    input      [31:0] b,
    output     [31:0] c
);

    reg [31:0] d;
    assign c = d;
    
    always @(posedge clk or negedge rst_n) begin
        if (!rst_n) begin
            d <= 32'd0;
        end else begin
            d <= a + b;
        end
    end

endmodule