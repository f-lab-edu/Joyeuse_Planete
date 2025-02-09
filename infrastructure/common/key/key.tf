resource "aws_key_pair" "key" {
  key_name   = "instance_key"
  public_key = file("${path.module}/EC2_KEY.pub")
}